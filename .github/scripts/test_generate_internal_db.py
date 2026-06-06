#!/usr/bin/env python3
"""Unit tests for generate_internal_db.py (the database code generator)."""

import os
import sys
import unittest

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import generate_internal_db as gen  # noqa: E402


class SourceNameToEnumTests(unittest.TestCase):
    def test_normalizes_punctuation_and_case(self):
        self.assertEqual(gen.source_name_to_enum("F-Droid (IzzyOnDroid)"), "F_DROID_IZZYONDROID")

    def test_prefixes_leading_digit(self):
        self.assertTrue(gen.source_name_to_enum("4chan").startswith("SOURCE_"))


class ValidationTests(unittest.TestCase):
    def test_accepts_valid_fingerprint(self):
        gen.validate_fingerprint("06:7A:40:C4:19")  # does not raise

    def test_rejects_fingerprint_with_quote(self):
        with self.assertRaises(ValueError):
            gen.validate_fingerprint('AA:BB","; evil()')

    def test_rejects_fingerprint_with_newline(self):
        with self.assertRaises(ValueError):
            gen.validate_fingerprint("AA\nBB")

    def test_accepts_valid_package(self):
        gen.validate_package("org.privacyguides.verifiedapps")  # does not raise

    def test_rejects_package_with_injection(self):
        with self.assertRaises(ValueError):
            gen.validate_package('com.evil") { drop() } //')


class FormatEntryTests(unittest.TestCase):
    DISPLAY_TO_ENUM = {"GitHub": "GITHUB"}

    def _sig(self, fingerprint, source="GitHub"):
        return {"fingerprint": fingerprint, "source": source}

    def test_emits_package_and_fingerprint(self):
        entry = gen.format_entry(
            "com.example.app",
            [self._sig("AA:BB:CC")],
            self.DISPLAY_TO_ENUM,
        )
        self.assertIn('"com.example.app"', entry)
        self.assertIn('"AA:BB:CC"', entry)
        self.assertIn("Source.GITHUB", entry)

    def test_rejects_malicious_fingerprint(self):
        with self.assertRaises(ValueError):
            gen.format_entry(
                "com.example.app",
                [self._sig('AA"); System.exit(1) //')],
                self.DISPLAY_TO_ENUM,
            )

    def test_returns_none_when_no_usable_signatures(self):
        self.assertIsNone(
            gen.format_entry("com.example.app", [self._sig("")], self.DISPLAY_TO_ENUM)
        )


if __name__ == "__main__":
    unittest.main()
