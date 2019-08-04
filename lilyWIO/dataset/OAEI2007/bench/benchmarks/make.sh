#!/bin/sh
# $Id: make.sh,v 1.27 2007/05/15 21:44:10 euzenat Exp $
# XSLT based test generation.
# //pass1: generate test files
# //pass2: fix URI
# //pass3: generate HTML
# //pass4: put everything on the web site (to be processed manually)

VERSION=40
CURRENT=2007/benchmarks
#CURRENT=tests

echo Generating files

# //pass1: generate test files

#####################################################################
# 101) Concept test: Id

#DONE!
/bin/rm -rf 101
mkdir 101
cp onto.rdf 101/
chmod 664 101/onto.rdf
cp refalign.rdf 101/
chmod 664 101/refalign.rdf

#####################################################################
# 102) Concept test: ?
# JUST TO BE COPIED ONCE

#####################################################################
# 103) Concept test: Language generalisation

#DONE!
/bin/rm -rf 103
mkdir 103
xsltproc xslt/generalization.xsl onto.rdf > 103/onto.rdf
cp 101/refalign.rdf 103/refalign.rdf

#####################################################################
# 104) Concept test: Language restriction

#DONE
\rm -rf 104
mkdir 104
xsltproc xslt/restriction.xsl onto.rdf > 104/onto.rdf
cp 101/refalign.rdf 104/refalign.rdf

#####################################################################
# 201) Systematic: No names
#Each label or identifier is replaced by a random one.

#DONE!
\rm -rf 201
mkdir 201
xsltproc xslt/trans-random.xsl onto.rdf > 201/onto.rdf
xsltproc xslt/trans-random.xsl refalign.rdf > 201/refalign.rdf

#####################################################################
# 202) Systematic: No names, no comment
#Each label or identifier is replaced by a random one. Comments  (rdfs:comment and dc:description) have been suppressed as well.

#DONE!
\rm -rf 202
mkdir 202
xsltproc xslt/strip-comments.xsl 201/onto.rdf > 202/onto.rdf
xsltproc xslt/trans-random.xsl refalign.rdf > 202/refalign.rdf

#####################################################################
# 203) Systematic: No comment (was Misspelling)

#DONE!
\rm -rf 203
mkdir 203
xsltproc xslt/strip-comments.xsl onto.rdf > 203/onto.rdf
cp 101/refalign.rdf 203/refalign.rdf

#####################################################################
# 204) Systematic: Naming conventions
#Different naming conventions (Uppercasing, underscore, dash, etc.)  are used for labels. Comments have been suppressed.

#DONE!
\rm -rf 204
mkdir 204
xsltproc xslt/trans-conv.xsl onto.rdf > 204/onto.rdf
xsltproc xslt/trans-conv.xsl refalign.rdf > 204/refalign.rdf

#####################################################################
# 205) Systematic: Synonyms
# Labels are replaced by synonyms. Comments have been suppressed.

#DONE!
\rm -rf 205
mkdir 205
xsltproc xslt/trans-synonyms.xsl onto.rdf > 205/onto.rdf
xsltproc xslt/trans-synonyms.xsl refalign.rdf > 205/refalign.rdf

#####################################################################
# 206) Systematic: Foreign names (including comments)
#The complete ontology is translated to another language than  english (French in the current case, but other languages would be fine).

#DONE! // NOMORECOMMENT TRANSLATIONS
\rm -rf 206
mkdir 206
xsltproc xslt/trans-foreign.xsl onto.rdf > 206/onto-iso8859.rdf
xsltproc xslt/trans-foreign.xsl refalign.rdf > 206/refalign-iso8859.rdf
cat 206/onto-iso8859.rdf | ./convert_iso2utf8.pl | sed "s:iso-8859-1:utf-8:"> 206/onto.rdf
cat 206/refalign-iso8859.rdf | ./convert_iso2utf8.pl | sed "s:iso-8859-1:utf-8:"> 206/refalign.rdf

#####################################################################
# 207) Systematic: Foreign names
#The complete ontology is translated to another language than  english (French in the current case, but other languages would be fine).

#DONE!
\rm -rf 207
mkdir 207
xsltproc xslt/trans-foreign.xsl onto.rdf > 207/onto-iso8859.rdf
cat 207/onto-iso8859.rdf | ./convert_iso2utf8.pl | sed "s:iso-8859-1:utf-8:"> 207/onto.rdf
cp 206/refalign.rdf 207/refalign.rdf
cp 206/refalign-iso8859.rdf 207/refalign-iso8859.rdf

#####################################################################
# 208) Systematic: Convention and no comments

#DONE!
\rm -rf 208
mkdir 208
xsltproc xslt/strip-comments.xsl 204/onto.rdf > 208/onto.rdf
cp 204/refalign.rdf 208/refalign.rdf

#####################################################################
# 209) Systematic: Synonyms and no comments

#DONE!
\rm -rf 209
mkdir 209
xsltproc xslt/strip-comments.xsl 205/onto.rdf > 209/onto.rdf
cp 205/refalign.rdf 209/refalign.rdf

#####################################################################
# 210) Systematic: Foreign language and no comments

#DONE!
\rm -rf 210
mkdir 210
xsltproc xslt/strip-comments.xsl 207/onto.rdf > 210/onto-iso8859.rdf
cat 210/onto-iso8859.rdf | ./convert_iso2utf8.pl | sed "s:iso-8859-1:utf-8:"> 210/onto.rdf
cp 207/refalign.rdf 210/refalign.rdf
cp 207/refalign-iso8859.rdf 210/refalign-iso8859.rdf

#####################################################################
# 221) Systematic: No hierarchy
#All subclass assertions to named classes are suppressed.
#(variation: compile inheritance)

#DONE!
\rm -rf 221
mkdir 221
xsltproc xslt/strip-subclass.xsl onto.rdf > 221/onto.rdf
cp 101/refalign.rdf 221/refalign.rdf

#####################################################################
# 222) Systematic: Flattened hierarchy
#A hierarchy still exists but has been strictly reduced.
# Suppressed Book, Part, Academic and Informal
# Replaced their apperance by Reference (incl. suppressed unionOf)
#inherited their properties

#DONE!
\rm -rf 222
mkdir 222
cp onto-flat.rdf 222/onto.rdf
chmod 644 222/onto.rdf
xsltproc xslt/flatten.xsl refalign.rdf > 222/refalign.rdf

#####################################################################
# 223) Systematic: Expanded hierarchy
#Numerous intermediate classes are introduced within the hierarchy.

#DONE!
\rm -rf 223
mkdir 223
cp onto-exp.rdf 223/onto.rdf
chmod 644 223/onto.rdf
cp 101/refalign.rdf 223/refalign.rdf

#####################################################################
# 224) Systematic: No instances
#All individuals have been suppressed from the ontology.

# DONE!
\rm -rf 224
mkdir 224
xsltproc xslt/strip-instances.xsl onto.rdf > 224/onto.rdf
cp 101/refalign.rdf 224/refalign.rdf

#####################################################################
# 225) Systematic: No restrictions
#All local restrictions on properties have been suppressed from the ontology.
#(variation: no property nor global restrictions on properties)

# DONE!
\rm -rf 225
mkdir 225
xsltproc xslt/strip-restrictions.xsl onto.rdf > 225/onto.rdf
cp 101/refalign.rdf 225/refalign.rdf

# 226) Systematic: No datatypes
#NOTTODO

# 227) Systematic: Unit differences
#NOTTODO

#####################################################################
# 228) Systematic: No properties
#Properties and relations between objects have been completely suppressed.
#(variation: leave the properties in instances)

#DONE!
\rm -rf 228
mkdir 228
xsltproc xslt/strip-properties.xsl onto.rdf > 228/onto.rdf
xsltproc xslt/strip-propalign.xsl refalign.rdf > 228/refalign.rdf

# 229) Systematic: Class vs instances
#NOTTODO

#####################################################################
# 230) Systematic: Flattening entities
#Some components of classes are expanded in the class structure  (e.g., year, month, day attributes instead of date).
#Here one limitation of the proposed format is that it does not cover  alignments such as: journalName = name o journal.

\rm -rf 230
mkdir 230
cp onto-cflat.rdf 230/onto.rdf
chmod 644 230/onto.rdf
cp refalign-cflat.rdf 230/refalign.rdf
chmod 644 230/refalign.rdf

#//TODO
# 231) Systematic: Multiplying entities
#Some classes are spreaded over several classes.
#Not available in this test.
\rm -rf 231
mkdir 231
cp 101/onto.rdf 231/onto.rdf
cp 101/refalign.rdf 231/refalign.rdf

#####################################################################
# 232) Systematic: No instances, No hierarchy

# DONE!
\rm -rf 232
mkdir 232
xsltproc xslt/strip-instances.xsl 221/onto.rdf > 232/onto.rdf
cp 221/refalign.rdf 232/refalign.rdf

#####################################################################
# 233) Systematic: No property, No hierarchy

# DONE!
\rm -rf 233
mkdir 233
xsltproc xslt/strip-properties.xsl 221/onto.rdf > 233/onto.rdf
xsltproc xslt/strip-propalign.xsl 221/refalign.rdf > 233/refalign.rdf

#####################################################################
# 234) Systematic: No hierarchy, flattened classes

# DONE!
\rm -rf 234
#mkdir 234
#xsltproc xslt/strip-subclass.xsl 230/onto.rdf > 234/onto.rdf
#cp 230/refalign.rdf 234/refalign.rdf

#####################################################################
# 235) Systematic: No hierarchy, expansed classes

# DONE!
\rm -rf 235
#mkdir 235
#xsltproc xslt/strip-subclass.xsl 231/onto.rdf > 235/onto.rdf
#cp 231/refalign.rdf 235/refalign.rdf

######################################################################
# 236) Systematic: No instances, No properties

#DONE!
\rm -rf 236
mkdir 236
xsltproc xslt/strip-properties.xsl 224/onto.rdf > 236/onto.rdf
xsltproc xslt/strip-propalign.xsl 224/refalign.rdf > 236/refalign.rdf

#####################################################################
# 237) Systematic: No instances, flattened classes

# DONE!
\rm -rf 237
mkdir 237
xsltproc xslt/strip-instances.xsl 222/onto.rdf > 237/onto.rdf
cp 222/refalign.rdf 237/refalign.rdf

#####################################################################
# 238) Systematic: No instances, expansed classes

# DONE!
\rm -rf 238
mkdir 238
xsltproc xslt/strip-instances.xsl 223/onto.rdf > 238/onto.rdf
cp 223/refalign.rdf 238/refalign.rdf

#####################################################################
# 239) Systematic: No properties, flattened classes

#DONE!
\rm -rf 239
mkdir 239
xsltproc xslt/strip-properties.xsl 222/onto.rdf > 239/onto.rdf
xsltproc xslt/strip-propalign.xsl 222/refalign.rdf > 239/refalign.rdf

#####################################################################
# 240) Systematic: No properties, expansed classes

#DONE!
\rm -rf 240
mkdir 240
xsltproc xslt/strip-properties.xsl 223/onto.rdf > 240/onto.rdf
xsltproc xslt/strip-propalign.xsl 223/refalign.rdf > 240/refalign.rdf

######################################################################
# 241) Systematic: No instances, No hierarchy, No properties

# DONE!
\rm -rf 241
mkdir 241
xsltproc xslt/strip-instances.xsl 233/onto.rdf > 241/onto.rdf
cp 233/refalign.rdf 241/refalign.rdf

#####################################################################
# 242) Systematic: No instances, No hierarchy, flattened classes

# DONE!
#\rm -rf 242
#mkdir 242
#xsltproc xslt/strip-instances.xsl 234/onto.rdf > 242/onto.rdf
#cp 234/refalign.rdf 242/refalign.rdf

#####################################################################
# 243) Systematic: No instances, No hierarchy, expansed classes

# DONE!
#\rm -rf 243
#mkdir 243
#xsltproc xslt/strip-instances.xsl 235/onto.rdf > 243/onto.rdf
#cp 235/refalign.rdf 243/refalign.rdf

######################################################################
# 244) Systematic: No properties, No hierarchy, flattened classes

#DONE!
#\rm -rf 244
#mkdir 244
#xsltproc xslt/strip-properties.xsl 234/onto.rdf > 244/onto.rdf
#xsltproc xslt/strip-propalign.xsl 234/refalign.rdf > 244/refalign.rdf


#####################################################################
# 245) Systematic: No properties, No hierarchy, expansed classes

#DONE!
#\rm -rf 245
#mkdir 245
#xsltproc xslt/strip-properties.xsl 235/onto.rdf > 245/onto.rdf
#xsltproc xslt/strip-propalign.xsl 235/refalign.rdf > 245/refalign.rdf

######################################################################
# 246) Systematic: No properties, No instances, flattened classes

#DONE!
\rm -rf 246
mkdir 246
xsltproc xslt/strip-properties.xsl 237/onto.rdf > 246/onto.rdf
xsltproc xslt/strip-propalign.xsl 237/refalign.rdf > 246/refalign.rdf

#####################################################################
# 247) Systematic: No properties, No instances, expansed classes

#DONE!
\rm -rf 247
mkdir 247
xsltproc xslt/strip-properties.xsl 238/onto.rdf > 247/onto.rdf
xsltproc xslt/strip-propalign.xsl 238/refalign.rdf > 247/refalign.rdf

####################################################################
# 248) Systematic: No names, no hierarchy

# DONE
\rm -rf 248
mkdir 248
xsltproc xslt/strip-subclass.xsl 202/onto.rdf > 248/onto.rdf
cp 202/refalign.rdf 248/refalign.rdf

#####################################################################
# 249) Systematic: No names, no instances

# DONE
\rm -rf 249
mkdir 249
xsltproc xslt/strip-instances.xsl 202/onto.rdf > 249/onto.rdf
cp 202/refalign.rdf 249/refalign.rdf

#####################################################################
# 250) Systematic: No names, no properties

#DONE!
\rm -rf 250
mkdir 250
xsltproc xslt/strip-properties.xsl 202/onto.rdf > 250/onto.rdf
xsltproc xslt/strip-propalign.xsl 202/refalign.rdf > 250/refalign.rdf

#####################################################################
# 251) Systematic: No names, flattened classes

#DONE
\rm -rf 251
mkdir 251
xsltproc xslt/strip-comments.xsl 222/onto.rdf > 251/preonto.rdf
xsltproc xslt/trans-random.xsl 251/preonto.rdf > 251/onto.rdf
xsltproc xslt/trans-random.xsl 222/refalign.rdf > 251/refalign.rdf

#####################################################################
# 252) Systematic: No names, expanded classes

#DONE
\rm -rf 252
mkdir 252
xsltproc xslt/strip-comments.xsl 223/onto.rdf > 252/preonto.rdf
xsltproc xslt/trans-random.xsl 252/preonto.rdf > 252/onto.rdf
xsltproc xslt/trans-random.xsl 223/refalign.rdf > 252/refalign.rdf

#####################################################################
# 253) Systematic: No names, no hierarchy, no instance

#DONE!
\rm -rf 253
mkdir 253
xsltproc xslt/strip-instances.xsl 248/onto.rdf > 253/onto.rdf
cp 248/refalign.rdf 253/refalign.rdf

#####################################################################
# 254) Systematic: No names, no hierarchy, no properties

#DONE
\rm -rf 254
mkdir 254
xsltproc xslt/strip-properties.xsl 248/onto.rdf > 254/onto.rdf
xsltproc xslt/strip-propalign.xsl 248/refalign.rdf > 254/refalign.rdf

#####################################################################
# 255) Systematic: No names, No hierarchy, flattened classes

#DONE
#\rm -rf 255
#mkdir 255
#xsltproc xslt/strip-subclass.xsl 251/onto.rdf > 255/onto.rdf
#cp 251/refalign.rdf 255/refalign.rdf

#####################################################################
# 256) Systematic: No names, No hierarchy, expanded classes

#DONE
#\rm -rf 256
#mkdir 256
#xsltproc xslt/strip-subclass.xsl 252/onto.rdf > 256/onto.rdf
#cp 252/refalign.rdf 256/refalign.rdf

#####################################################################
# 257) Systematic: No names, no instances, no properties

#DONE
\rm -rf 257
mkdir 257
xsltproc xslt/strip-properties.xsl 249/onto.rdf > 257/onto.rdf
xsltproc xslt/strip-propalign.xsl 249/refalign.rdf > 257/refalign.rdf

#####################################################################
# 258) Systematic: No names, No instances, flattened classes

#DONE
\rm -rf 258
mkdir 258
xsltproc xslt/strip-instances.xsl 251/onto.rdf > 258/onto.rdf
cp 251/refalign.rdf 258/refalign.rdf

#####################################################################
# 259) Systematic: No names, No instances, expanded classes

#DONE
\rm -rf 259
mkdir 259
xsltproc xslt/strip-instances.xsl 252/onto.rdf > 259/onto.rdf
cp 252/refalign.rdf 259/refalign.rdf

#####################################################################
# 260) Systematic: No names, No properties, flattened classes

#DONE
\rm -rf 260
mkdir 260
xsltproc xslt/strip-properties.xsl 251/onto.rdf > 260/onto.rdf
xsltproc xslt/strip-propalign.xsl 251/refalign.rdf > 260/refalign.rdf

#####################################################################
# 261) Systematic: No names, No properties, expanded classes

#DONE
\rm -rf 261
mkdir 261
xsltproc xslt/strip-properties.xsl 252/onto.rdf > 261/onto.rdf
xsltproc xslt/strip-propalign.xsl 252/refalign.rdf > 261/refalign.rdf

#####################################################################
# 262) Systematic: No names, no instances, no properties

#DONE
\rm -rf 262
mkdir 262
xsltproc xslt/strip-properties.xsl 253/onto.rdf > 262/onto.rdf
xsltproc xslt/strip-propalign.xsl 253/refalign.rdf > 262/refalign.rdf

#####################################################################
# 263) Systematic: No names, No instances, No hierarchy, flattened classes

#DONE
#\rm -rf 263
#mkdir 263
#xsltproc xslt/strip-instances.xsl 255/onto.rdf > 263/onto.rdf
#cp 255/refalign.rdf 263/refalign.rdf

#####################################################################
# 264) Systematic: No names, No instances, No hierarchy, expanded classes

#DONE
#\rm -rf 264
#mkdir 264
#xsltproc xslt/strip-instances.xsl 256/onto.rdf > 264/onto.rdf
#cp 256/refalign.rdf 264/refalign.rdf

#####################################################################
# 265) Systematic: No names, No instances, No hierarchy, flattened classes

#DONE
\rm -rf 265
mkdir 265
xsltproc xslt/strip-instances.xsl 260/onto.rdf > 265/onto.rdf
cp 260/refalign.rdf 265/refalign.rdf

#####################################################################
# 266) Systematic: No names, No instances, No hierarchy, expanded classes

#DONE
\rm -rf 266
mkdir 266
xsltproc xslt/strip-instances.xsl 261/onto.rdf > 266/onto.rdf
cp 261/refalign.rdf 266/refalign.rdf

#####################################################################
# 267) Systematic: Nothing, flattened classes

#DONE
#\rm -rf 267
#mkdir 267
#xsltproc xslt/strip-subclass.xsl 265/onto.rdf > 267/onto.rdf
#cp 265/refalign.rdf 267/refalign.rdf

#####################################################################
# 268) Systematic: Nothing, expanded classes

#DONE
#\rm -rf 268
#mkdir 268
#xsltproc xslt/strip-subclass.xsl 266/onto.rdf > 268/onto.rdf
#cp 266/refalign.rdf 268/refalign.rdf

#####################################################################
# //pass2: fix URI
# DONE!

echo -n "Fixing URI "

for i in `ls -d [1-2][0-9][0-9]` 
do
echo -n "*"$i"*"
if [ -f $i/refalign.rdf ]
then
ed -s $i/onto.rdf << EOF &>/dev/null
1,$ s;/tests/101/onto.rdf;/tests/$i/onto.rdf;g
w
EOF
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;<uri2>http://oaei.ontologymatching.org/tests/101/onto.rdf</uri2>;<uri2>http://oaei.ontologymatching.org/tests/$i/onto.rdf</uri2>;g
w
EOF
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;<onto2>file:///Web/html/oaei/tests/101/onto.rdf</onto2>;<onto2>file:///Web/html/oaei/tests/$i/onto.rdf</onto2>;g
w
EOF
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;entity2 rdf:resource=\(['"]\)http://oaei.ontologymatching.org/tests/101/;entity2 rdf:resource=\1http://oaei.ontologymatching.org/tests/$i/;g
w
EOF
fi
if [ -f $i/refalign-iso8859.rdf ]
then
ed -s $i/onto-iso8859.rdf << EOF &>/dev/null
1,$ s;/tests/101/onto.rdf;/tests/$i/onto.rdf;g
w
EOF
ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
1,$ s;<uri2>http://oaei.ontologymatching.org/tests/101/onto.rdf</uri2>;<uri2>http://oaei.ontologymatching.org/tests/$i/onto.rdf</uri2>;g
w
EOF
# Beware, this is different here...
ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
1,$ s;<onto2>file:///Web/html/oaei/tests/101/onto.rdf</onto2>;<onto2>file:///Web/html/oaei/tests/$i/onto-iso8859.rdf</onto2>;g
w
EOF
ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
1,$ s;entity2 rdf:resource=\(['"]\)http://oaei.ontologymatching.org/tests/101/;entity2 rdf:resource=\1http://oaei.ontologymatching.org/tests/$i/;g
w
EOF
fi
done

for i in `ls -d [1-2][0-9][0-9]` 
do
ed -s $i/onto.rdf << EOF &>/dev/null
1,$ s;oaei.ontologymatching.org/tests/;oaei.ontologymatching.org/$CURRENT/;g
w
EOF
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;oaei.ontologymatching.org/tests/;oaei.ontologymatching.org/$CURRENT/;g
w
EOF
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;file:///Web/html/oaei/tests/;http://oaei.ontologymatching.org/$CURRENT/;g
w
EOF
if [ -f $i/onto-iso8859.rdf ]
then
ed -s $i/onto-iso8859.rdf << EOF &>/dev/null
1,$ s;oaei.ontologymatching.org/tests/;oaei.ontologymatching.org/$CURRENT/;g
w
EOF
ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
1,$ s;oaei.ontologymatching.org/tests/;oaei.ontologymatching.org/$CURRENT/;g
w
EOF
ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
1,$ s;file:///Web/html/oaei/tests/;http://oaei.ontologymatching.org/$CURRENT/;g
w
EOF
fi
done

#####################################################################
# //pass3: generate HTML
# DONE!

echo
echo -n "Generating HTML "

for i in `ls -d [0-9][0-9][0-9]` 
do
echo -n "*"$i"*"
xsltproc xslt/form-align.xsl $i/refalign.rdf > $i/refalign.html
xsltproc xslt/owl2html.xsl $i/onto.rdf > $i/onto.html
done

echo

#####################################################################
# //pass4: generate ZIP file
# DONE!

echo "Be sure that CURRENT was correctly set ("$CURRENT")"
echo "Apply pass4 manually with the correct VERSION ("$VERSION")"
echo "Check that the 3xx series is in the appropriate form (files contain correct URI)"

exit

# BEWARE: THIS LAST PART CANNOT BE BLINDLY APPLIED

# copy

cd ../..

\rm -rf $CURRENT.old
mv $CURRENT $CURRENT.old

cp -rf lib $CURRENT

# THIS HAS NOW BEEN PREPARED IN ADVANCE
#cd $CURRENT

#for i in `ls -d [0-9][0-9][0-9]` 
#do
#echo -n "*"$i"*"
#ed -s $i/refalign.rdf << EOF &>/dev/null
#1,$ s;file:///Web/html/oaei/tests;http://oaei.ontologymatching.org/$CURRENT;g
#w
#EOF
#if [ -f $i/refalign-iso8859.rdf ]
#then
#ed -s $i/refalign-iso8859.rdf << EOF &>/dev/null
#1,$ s;file:///Web/html/oaei/tests;http://oaei.ontologymatching.org/$CURRENT;g
#w
#EOF
#fi
#done
#echo

#cd ..

cd $CURRENT
/bin/rm bench.zip
cd ..
zip bench$VERSION.zip -r  benchmarks/ -x benchmarks/RCS/* benchmarks/xslt/RCS/* benchmarks/NEW305/*
mv bench$VERSION.zip ../versions
cp ../versions/bench$VERSION.zip benchmarks/bench.zip

#java -cp /Volumes/Phata/JAVA/alignapi/lib/procalign.jar fr.inrialpes.exmo.align.util.GroupAlign -o inria -n file:///Web/html/oaei/2006/benchmarks/101/onto.rdf -i fr.inrialpes.exmo.align.impl.method.StringDistAlignment

for i in `ls -d [0-9][0-9][0-9]`
do
ed -s $i/refalign.rdf << EOF &>/dev/null
1,$ s;<onto1>http://oaei.ontologymatching.org/2006;<onto1>file:///Java/TEST-ALIGN;g
1,$ s;<onto2>http://oaei.ontologymatching.org/2006;<onto2>file:///Java/TEST-ALIGN;g
w
EOF
done
