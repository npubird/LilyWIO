#!/usr/bin/perl
#
# Written by Marc Liyanage (http://www.entropy.ch)
#

print iso2utf8(join("", <>));

sub iso2utf8 {

        my ($string) = @_;
		my %cache = ();
		
		# Match characters with their high bit set (0x80-0xff).
		# Send these to the encode function and replace
		# them with the result of the function.
		#
        $string =~ s/([\x80-\xff])/$cache{$1} ||= encode_iso($1)/eg;
        return $string;

}

sub encode_iso {

	# Take the integer value of the ISO character
	# using the ord() function
	#
	my $iso = ord($_[0]);

	# Take the two high bits (0xc0) of the iso value.
	# Shift them to the right by 6 positions and OR
	# it with the UTF-8 bit pattern for a two-byte sequence
	# (110xxxxx or 0xc0). This gives the leading byte of the
	# 2-byte UTF-8 sequence.
	#
	# Then take the ISO value again, keep the 6 lower bits
	# (0x3f) and OR the resulting value with the bit pattern
	# for an UTF-8 trailing byte (10xxxxxx or 0x80).
	# This is the second byte of the UTF-8 sequence.
	#
	my $a = ((($iso & 0xc0) >> 6) | 0xc0);
	my $b = ($iso & 0x3f) | 0x80;


	# Look up the character for the two bytes, concatenate them
	# and return this two-character sequence
	#
	return chr($a) . chr($b);

}



