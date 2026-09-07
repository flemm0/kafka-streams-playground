To fix annoying import not found errors:

1. Refresh the Bloop configuration (regenerates the files under `.bloop/`):
```bash
mvn generate-sources ch.epfl.scala:bloop-maven-plugin:2.0.5:bloopInstall
```

2. Open VSCode Command Palette

3. Run `Metals: Restart Build Server`

4. If needed, run `Metals: Import Build`
