#!/usr/bin/perl -w

use strict;

die "Need at least one argument: names of files to update" if ((scalar @ARGV) == 0);

foreach my $f ( @ARGV ) {
  die "File does not exist: $f" unless (-f $f);
  die "File $f is not writable" unless (-w $f);
}

foreach my $f ( @ARGV ) {
  my $s = getContent($f);
  $s =~ s/extendedgazetteer2\.ExtendedGazetteer2/extendedgazetteer.ExtendedGazetteer/g;
  $s =~ s/extendedgazetteer2\.FeatureGazetteer/extendedgazetteer.FeatureGazetteer/g;
  $s =~ s/extendedgazetteer2\.FeatureGazetteerProcessingMode/extendedgazetteer.ExtendedGazetteerProcessingMode/g;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchStartFeature</string>\s*\n\s*<null/>\s*\n\s*</entry>\n}{}mg;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchStartFeature</string>\s*\n\s*<string></string>\s*\n\s*</entry>\n}{}mg;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchEndFeature</string>\s*\n\s*<null/>\s*\n\s*</entry>\n}{}mg;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchEndFeature</string>\s*\n\s*<string></string>\s*\n\s*</entry>\n}{}mg;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchTypeFeature</string>\s*\n\s*<null/>\s*\n\s*</entry>\n}{}mg;
  $s =~ s{^\s*<entry>\s*\n\s*<string>matchTypeFeature</string>\s*\n\s*<string></string>\s*\n\s*</entry>\n}{}mg;
  open(OUT,">",$f) or die "Could not open file $f for writing: $!";
  print OUT $s;
  close OUT;
}

sub getContent {
  my $f = shift;
  local $/ = undef;
  open(IN,"<",$f) or die "Could not open file $f for reading: $!";
  binmode IN;
  my $ret = <IN>;
  close IN;
  return $ret;
}
