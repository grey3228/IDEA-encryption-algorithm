\\ -- Compilation key scheme -- \\

To encrypt/decrypt file/directory use key scheme:
	java StartIDEA 128-bit_key file/directory_name -mode,
	where -mode is a flag with two possible values:
	- "-e" - for encrypting
	- "-d" - for decrypting 

\\ -- 	-- \\



\\ -- Correct examples -- \\ 

	java StartIDEA 12345678 file.txt -e
	java StartIDEA abcde111 someFolder -d

\\ --   -- \\


\\ -- Correctness approvment -- \\

See TestClass.java which includes checking this algorithm for correctness with official control values. 
Compile it and then launch without any arguments to start checking.

\\ --   -- \\
