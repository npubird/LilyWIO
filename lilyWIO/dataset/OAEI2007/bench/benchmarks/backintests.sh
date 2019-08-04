#!/bin/sh
# 2007 ready

echo "1) update informative part of HTML files"
echo "2) run make.sh with CURRENT=tests"
echo "3) copy the current set "
echo "4) run the following script to update HTML files"
echo "5) run the manual part of make.sh to generate bench.zip"

exit

co -l index.html
ed index.html <<EOF
1,$ s:\.\./\.\.:\.\.:g
1,$ s:2007/benchmarks:tests:g
w
q
EOF
ci -u -q -m"updated test" index.html

co -l logs.html
ed logs.html <<EOF
1,$ s:\.\./\.\.:\.\.:g
1,$ s:2007/benchmarks:tests:g
w
q
EOF
ci -u -q -m"updated test" logs.html

for d in 301 302 303 304
do
ed $d/refalign.rdf <<EOF
1,$ s:2007/benchmarks:tests:g
w
q
EOF
ed $d/refalign.html <<EOF
1,$ s:2007/benchmarks:tests:g
w
q
EOF
ed $d/onto.rdf <<EOF
1,$ s:2007/benchmarks:tests:g
w
q
EOF
done
