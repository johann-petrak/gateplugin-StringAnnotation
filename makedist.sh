#!/bin/bash

name=StringAnnotation
tmpdir=/tmp
curdir=`pwd -P`
version=`perl -n -e 'if (/VERSION="([^"]+)"/) { print $1;}' < $curdir/creole.xml`
destdir=$tmpdir/${name}$$

rm -rf "$destdir"
mkdir -p $destdir/$name
rm -f $name-*.zip
rm -f $name-*.tgz
git archive --format zip --output ${name}-${version}-src.zip --prefix=$name/ master
pushd $destdir
unzip $curdir/${name}-${version}-src.zip
cd $name
cp $curdir/build.properties .
ant || exit
ant clean.classes || exit
rm -rf build.properties
rm -rf makedist.sh
rm -rf test
cd ..
## find $name -name '.svn' | xargs -n 1 rm -rf
zip -r $curdir/$name-$version.zip $name
# cp $curdir/$name-$version.zip $curdir/creole.zip
# tar zcvf $curdir/$name-$version.tgz $name
# cp $curdir/creole.zip $name
# mv $name $name-$version
# tar zcvf $curdir/$name-$version-updatesite.tgz $name-$version
popd
