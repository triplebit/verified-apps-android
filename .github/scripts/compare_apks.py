#!/usr/bin/env python3
"""Compare two APKs file-by-file, excluding signatures."""
import zipfile
import hashlib
import sys

original = sys.argv[1]
rebuilt = sys.argv[2]

a = zipfile.ZipFile(original)
b = zipfile.ZipFile(rebuilt)

a_names = sorted(n for n in a.namelist() if not n.startswith('META-INF/'))
b_names = sorted(n for n in b.namelist() if not n.startswith('META-INF/'))

if a_names != b_names:
    print('--- Different file lists ---')
    for n in set(a_names) - set(b_names):
        print('Only in original: ' + n)
    for n in set(b_names) - set(a_names):
        print('Only in rebuilt:  ' + n)
    print()

diffs = []
for name in a_names:
    if name in b_names:
        ha = hashlib.sha256(a.read(name)).hexdigest()
        hb = hashlib.sha256(b.read(name)).hexdigest()
        if ha != hb:
            diffs.append((name, a.getinfo(name).file_size, b.getinfo(name).file_size, ha, hb))

if diffs:
    print(f'{len(diffs)} files differ:')
    for name, sa, sb, ha, hb in diffs:
        print(f'  {name}  orig={sa} rebuilt={sb}')
else:
    print('All non-signature files match!')

a.close()
b.close()

sys.exit(1 if (a_names != b_names or diffs) else 0)
