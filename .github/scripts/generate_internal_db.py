#!/usr/bin/env python3
"""Replace InternalVerificationInfoDatabase.kt from privacyguides/verified-apps data.yml."""

import argparse
import codecs
import re
import sys
from pathlib import Path

import yaml

REPO_ROOT = Path(__file__).resolve().parents[2]
KOTLIN_SOURCE_FILE = (
    REPO_ROOT
    / "app/src/main/kotlin/org/privacyguides/verifiedapps/InternalVerificationInfoDatabase.kt"
)

SOURCE_MAP = {
    "Accrescent": "ACCRESCENT",
    "Google Play": "GOOGLE_PLAY_STORE",
    "Google Play Store": "GOOGLE_PLAY_STORE",
    "F-Droid": "FDROID",
    "F-Droid (Custom)": "FDROID",
    "F-Droid (IzzyOnDroid)": "FDROID_IZZYONDROID",
    "GitHub": "GITHUB",
    "Codeberg": "CODEBERG",
    "GitLab": "GITLAB",
    "App's F-Droid Repo": "APP_FDROID_REPO",
    "App's Website": "WEBSITE",
    "Developer's Website": "WEBSITE",
    "Google Pixel OS": "GOOGLE_PIXEL_OS",
    "Direct APK Link": "DIRECT_APK_LINK",
    "AppVerifier": "APPVERIFIER",
    "Verified Apps": "VERIFIED_APPS",
}

SUPPORTED_SCHEMA_VERSIONS = {2, 3}

S4 = "    "
S8 = "        "
S12 = "            "
S16 = "                "
S20 = "                    "


def source_name_to_enum(name: str) -> str:
    result = name.strip().upper()
    result = re.sub(r"[^A-Z0-9]", "_", result)
    result = re.sub(r"_+", "_", result)
    result = result.strip("_")
    if not result or result[0].isdigit():
        result = "SOURCE_" + result
    return result


def privacyguides_to_source(name: str, display_to_enum: dict[str, str]) -> str | None:
    name = name.strip()
    if name in display_to_enum:
        return "Source." + display_to_enum[name]
    return None


def load_yaml_file(path: Path):
    with open(path, encoding="utf-8") as f:
        return yaml.safe_load(f)


def find_unknown_sources(privacyguides_data: list) -> list[str]:
    unknown: set[str] = set()
    for app in privacyguides_data:
        for sig in app.get("signature", []):
            sources = sig.get("sources", [])
            if sources:
                for s in sources:
                    name = s.get("name", "").strip()
                    if name and name not in SOURCE_MAP:
                        unknown.add(name)
            else:
                name = sig.get("source", "").strip()
                if name and name not in SOURCE_MAP:
                    unknown.add(name)
    return sorted(unknown)


def build_display_to_enum(privacyguides_data: list) -> dict[str, str]:
    """Map every source display name in data.yml to a Source enum constant name."""
    display_to_enum: dict[str, str] = dict(SOURCE_MAP)
    used_enum_names: set[str] = set(display_to_enum.values())

    for name in find_unknown_sources(privacyguides_data):
        enum_val = source_name_to_enum(name)
        unique_val = enum_val
        suffix = 1
        while unique_val in used_enum_names:
            unique_val = f"{enum_val}_{suffix}"
            suffix += 1
        display_to_enum[name] = unique_val
        used_enum_names.add(unique_val)

    return display_to_enum


def enum_entries_for_kotlin(display_to_enum: dict[str, str]) -> list[tuple[str, str]]:
    """One enum entry per constant: enum name -> canonical display label."""
    enum_to_display: dict[str, str] = {}
    for display, enum_val in display_to_enum.items():
        if enum_val not in enum_to_display or len(display) < len(enum_to_display[enum_val]):
            enum_to_display[enum_val] = display
    return sorted(enum_to_display.items(), key=lambda item: item[0])


def format_source_enum(display_to_enum: dict[str, str]) -> str:
    lines = ['enum class Source(val displayName: String) {', '    NONE("NONE"), // DO NOT USE IN DATABASE ENTRIES.']
    for enum_val, display in enum_entries_for_kotlin(display_to_enum):
        escaped = display.replace("\\", "\\\\").replace('"', '\\"')
        lines.append(f'    {enum_val}("{escaped}"),')
    lines.append("}")
    return "\n".join(lines)


def format_entry(package: str, signatures: list, display_to_enum: dict[str, str]) -> str | None:
    fp_to_sources: dict[str, set[str]] = {}

    for sig in signatures:
        raw_fingerprint = sig.get("fingerprint", "").strip()
        if not raw_fingerprint:
            continue

        sources = sig.get("sources", [])
        if sources:
            source_names = [s.get("name", "").strip() for s in sources]
        else:
            source_names = [sig.get("source", "").strip()]

        for raw_name in source_names:
            source = privacyguides_to_source(raw_name, display_to_enum)
            if source is None:
                continue
            for fingerprint in raw_fingerprint.splitlines():
                fingerprint = fingerprint.strip()
                if not fingerprint:
                    continue
                if fingerprint not in fp_to_sources:
                    fp_to_sources[fingerprint] = set()
                fp_to_sources[fingerprint].add(source)

    if not fp_to_sources:
        return None

    source_set_to_fps: dict[frozenset[str], list[str]] = {}
    for fingerprint, sources in fp_to_sources.items():
        key = frozenset(sources)
        if key not in source_set_to_fps:
            source_set_to_fps[key] = []
        source_set_to_fps[key].append(fingerprint)

    hashes_blocks = []
    for source_set, fingerprints in sorted(
        source_set_to_fps.items(), key=lambda item: sorted(item[1])[0]
    ):
        sorted_sources = sorted(source_set)
        sorted_fps = sorted(fingerprints)
        source_lines = ",\n".join(f"{S20}{s}" for s in sorted_sources)
        fp_lines = ",\n".join(f'{S20}"{fp}"' for fp in sorted_fps)
        hashes_blocks.append(
            f"""{S12}Hashes(
{S16}listOf(
{source_lines}
{S16}),
{S16}listOf(
{fp_lines}
{S16}),
{S16}false
{S12})"""
        )

    if len(fp_to_sources) > 1:
        all_sources: set[str] = set()
        for sources in fp_to_sources.values():
            all_sources.update(sources)
        sorted_sources = sorted(all_sources)
        sorted_all_fps = sorted(fp_to_sources.keys())
        source_lines = ",\n".join(f"{S20}{s}" for s in sorted_sources)
        fp_lines = ",\n".join(f'{S20}"{fp}"' for fp in sorted_all_fps)
        hashes_blocks.append(
            f"""{S12}Hashes(
{S16}listOf(
{source_lines}
{S16}),
{S16}listOf(
{fp_lines}
{S16}),
{S16}false
{S12})"""
        )

    joined = ",\n".join(hashes_blocks)
    return (
        f"{S4}InternalDatabaseVerificationInfo(\n"
        f'{S8}"{package}",\n'
        f"{S8}listOf(\n"
        f"{joined}\n"
        f"{S8})\n"
        f"{S4})"
    )


def read_doc_comment() -> str:
    text = KOTLIN_SOURCE_FILE.read_text(encoding="utf-8")
    match = re.search(
        r"/\*\*.*?\*/\s*data class InternalDatabaseVerificationInfo",
        text,
        re.DOTALL,
    )
    if not match:
        raise RuntimeError("Could not find database documentation comment in Kotlin source.")
    return match.group(0).removesuffix("data class InternalDatabaseVerificationInfo").rstrip()


def generate_kotlin(privacyguides_data: list) -> str:
    display_to_enum = build_display_to_enum(privacyguides_data)
    doc_comment = read_doc_comment()

    entries: list[str] = []
    for app in sorted(privacyguides_data, key=lambda item: item.get("package", "").lower()):
        package = app.get("package", "")
        signatures = app.get("signature", [])
        if not package or not signatures:
            continue
        entry = format_entry(package, signatures, display_to_enum)
        if entry:
            entries.append(entry)

    body = ",\n".join(entries)
    source_enum = format_source_enum(display_to_enum)

    return "\n".join(
        [
            "package org.privacyguides.verifiedapps",
            "",
            "import org.privacyguides.verifiedapps.data.Hashes",
            "",
            "",
            source_enum,
            "",
            doc_comment,
            "data class InternalDatabaseVerificationInfo(",
            "    val packageName: String,",
            "    val hashesList: List<Hashes>",
            ")",
            "",
            "/**",
            " * The internal verification info database.",
            " *",
            " * Generated from https://github.com/privacyguides/verified-apps/blob/main/data.yml",
            " */",
            "val internalVerificationInfoDatabase = setOf(",
            body,
            ")",
            "",
        ]
    )


def check_schema(raw) -> None:
    schema = raw.get("schema") if isinstance(raw, dict) else None
    if schema is not None and schema not in SUPPORTED_SCHEMA_VERSIONS:
        print(
            f"warning: unexpected schema version {schema}, "
            f"expected one of {sorted(SUPPORTED_SCHEMA_VERSIONS)}",
            file=sys.stderr,
        )


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Replace InternalVerificationInfoDatabase.kt from privacyguides/verified-apps data.yml."
    )
    parser.add_argument(
        "--data-yml",
        metavar="PATH",
        required=True,
        help="Path to privacyguides/verified-apps data.yml",
    )
    args = parser.parse_args()

    privacyguides_data = load_yaml_file(Path(args.data_yml))
    check_schema(privacyguides_data)
    if isinstance(privacyguides_data, dict):
        privacyguides_data = privacyguides_data.get("packages", [])

    kotlin_text = generate_kotlin(privacyguides_data)
    KOTLIN_SOURCE_FILE.write_text(kotlin_text, encoding="utf-8", newline="\n")
    print(f"Wrote {len(privacyguides_data)} packages to {KOTLIN_SOURCE_FILE}")


if __name__ == "__main__":
    main()
