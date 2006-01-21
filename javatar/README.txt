Java tar is here because the manifest in the publically
available jar file will not work with Java WebStart.

None of the source has been changed.

The install ant target will put the constructed jar
in the correct location in the common project. This
only needs to be done if the content changes, which
it should never do.