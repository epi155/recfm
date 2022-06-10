#!/bin/bash 

function modelSync() {
    local source=$1
    local target=$2
    [ -d $source ] || exit 1
    [ -d $target ] || exit 2
    # clean target
    rm -v $target/*
    for f in $source/*
    do
      bn=$(basename "$f")
      sed -e '1,2d' < "$f" > "$target/$bn"
    done
}

modelSync recfm-lib-java/src/main/java/io/github/epi155/recfm/java/ \
          recfm-maven-plugin/src/main/resources/models/java/

modelSync recfm-lib-scala/src/main/scala/io/github/epi155/recfm/scala/ \
          recfm-maven-plugin/src/main/resources/models/scala/
