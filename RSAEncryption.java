//Klementina Stojanovska
//CSE 270M
//10/31/14

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RSAEncryption {

	static long p,q,n,e,d;
	static long[] encrypted;
	static char[] alphabet = {'A','B','C','D',
		'E','F','G','H','I','J','K','L','M',
		'N','O','P','Q','R','S','T','U','V','W','X',
		'Y','Z'};
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		generateKeys(in);
		System.out.println("");
		encrypt(in);
		System.out.println("");
		decrypt();
		in.close();
		
		ArrayList<Long> list = coprimes(256);
		int count = 0;
		for(Long e: list){
			count++;
		}
		System.out.println(count);
		printCoprimes(list);
	}

	//This is the code that generates the public
	//and private keys using user input
	public static void generateKeys(Scanner s){
		System.out.println("Part 1 - RSA KEY GENERATION");
		System.out.println("Enter 2 prime numbers, and I will "
				+ "help you generate public and private RSA keys.");
		
		//prompts user to enter a prime number 
		//if number is not prime, will print 
		//NOT PRIME and prompt user for another
		//number
		System.out.print("Enter prime p: ");
		p = s.nextLong();
		while(isPrime(p) != true){
			System.out.println("NOT PRIME!");
			System.out.print("Enter prime p: ");
			p = s.nextLong();
		}
		System.out.print("Enter prime q: ");
		q = s.nextLong();
		while(isPrime(q) != true){
			System.out.println("NOT PRIME!");
			System.out.print("Enter prime q: ");
			q = s.nextLong();
		}
		System.out.println("Thank you. Keep these primes secret");
		
		//Calculates n as the product of p and q and prints it.
		n = p*q;
		System.out.println("n = pq = " + n + " (share this value)");
		long numOfCoprimes = n-p-q+1;
		System.out.println("There are " + numOfCoprimes + " integers <= " + n 
				+ " that are coprime with " + n + ".");
		
		//Tells user to choose a value for e and asks
		//the user whether they would like to see a list of the
		//values that they can choose from. If user inputs "y", then
		//list of coprimes is printed.
		System.out.println("Choose a value for e.");
		System.out.println("Make sure 1 < e < " + numOfCoprimes + " and e is "
				+ "coprime with " + numOfCoprimes);
		System.out.print("Would you like to see a list of " + numOfCoprimes + "'s"
				+ " coprimes (y/n)?");
		String response = s.next();
		ArrayList<Long> listCoprimes = coprimes(numOfCoprimes);
		if(response.equals("y") || response.equals("Y")) printCoprimes(listCoprimes);
		
		//Prompts user to enter a number for e and then
		//checks if that number is coprime with the numOfCoprimes.
		//if not coprime, prompts the user to enter another number.
		System.out.print("Choose a value for e: ");
		e = s.nextLong();
		while(isCoprime(e,listCoprimes) != true){
			System.out.println("Make sure 1 < e < " + numOfCoprimes + " and e is "
					+ "coprime with " + numOfCoprimes);
			System.out.print("Choose a value for e: ");
			e = s.nextLong();
		}
		
		//Prints the inverse:
		d = inverse(e,numOfCoprimes);
		System.out.println("The inverse of " + e + ", mod " + numOfCoprimes + " is " + d); 
		
		//Prints the public key and private key
		System.out.println("PUBLIC KEY: e, n: " + e + ", " + n);
		System.out.println("PRIVATE KEY: d, n: " + d + ", " + n);
	}
	
	//This is the code that encrypts a message for
	//the user.
	public static void encrypt(Scanner s){
		System.out.println("Part 2 - RSA ENCRYPTION USING PUBLIC KEY");
		System.out.println("KEYS HAVE BEEN GENERATED. Let's encrypt a message.");
		
		//Prompts user to write a word to be encrypted
		System.out.print("Enter an uppercase word you would like to encrypt: ");
		String str = s.next();
		
		//converts the word into a number array then prints
		//the values in the array.
		long[] sToNum = stringToNum(str);
		System.out.println("Converted to numbers, your message is: " + Arrays.toString(sToNum));
		
		//This is where the values get encrypted to new values
		//After they are encrypted, it prints each value
		System.out.println("For each value, compute (value)^" +e + " mod " + n);
		encrypted = new long[sToNum.length];
		for(int i = 0; i< encrypted.length; i++){
			encrypted[i] = modPow(sToNum[i], e, n);	
		}
		
		System.out.println("Encrypted, your message is: " + Arrays.toString(encrypted));	
	}
	
	//This is the code that decrypts the encrypted
	//values to the original message.
	public static void decrypt(){
		System.out.println("Part 3 - RSA DECRYPTION USING PRIVATE KEY");
		System.out.println("Now, for each value, compute (value)^" + d + " mod " + n);
		
		//This uses the private keys to decrypt each value in
		//the encrypted array.
		long[] decrypted = new long[encrypted.length];
		for(int i = 0; i < decrypted.length; i++){
			decrypted[i] = modPow(encrypted[i], d, n);
		}
		
		//Prints the decrypted values and then converts the values
		//back to a String and prints the original message inputed by
		//the user.
		System.out.println("Decrypted, your message is: " + Arrays.toString(decrypted));
		String origText = numToString(decrypted);
		System.out.println("Converted back to text, your message is: " + origText);
		
	}
	
	//Checks whether a number is prime. First checks if 
	// a number is even or 1 and if it is returns false.
	//Then checks values up to the square root of n and if
	//n and that number is divisible, then returns false.
	//Otherwise returns true.
	public static boolean isPrime(long n){
		if(n%2 == 0 || n == 1) return false;
		int check = (int)Math.pow(n, .5);
		for(int i = 3; i<= check; i+=2){
			if(n % i == 0) return false;
		}
		return true;
	}
	
	//Returns the gcd of two number values.
	//Computes the gcd recursively.
	public static long gcd(long a, long b){
		if(b == 0) return a;
		return gcd(b, a%b);
	}
	
	//Creates an ArrayList that holds the coprimes of a number
	//a. Goes through the values up to a and if the
	//gcd of a and the value i is 1, then adds i to the
	//ArrayList. When loop is finished, returns that ArrayList.
	public static ArrayList<Long> coprimes(long a){
		ArrayList<Long> coprimeList = new ArrayList<Long>();
		for(long i = 1; i< a; i++){
			if(gcd(a,i) == 1) coprimeList.add(i);
		}
		return coprimeList;
	}
	
	//Takes a parameter ArrayList and goes through each
	//element in the ArrayList and prints it.
	public static void printCoprimes(ArrayList<Long> list){
		for(Long e: list){
			System.out.print(e.toString() + " ");
		}
		System.out.println("");
	}
	
	//Checks whether a value is a coprime listed in the
	//ArrayList. Goes through each element in the ArrayList and
	//if an element in the list is equal to the value you are searching for
	//then returns true. Otherwise, returns false.
	public static boolean isCoprime(long input, ArrayList<Long> list){
		for(Long e: list){
			if(e == input) return true;
		}
		return false;
	}
	
	//This method finds the inverse of e, mod m.
	//Uses variables to keep track of the remainder,
	//quotient, and values s and t for present case
	//and two cases before it. s and t are values in which
	//in the example GCD(8,25) -> 1, 1 = s(8) + t(25).
	//Goes through until remainder is 0. Once remainder
	//is 0, checks the previous remainder. If the previous
	//remainder is not 1, then throws an exception. Otherwise,
	//checks sOld and if it is negative, adds amount m to it
	//to make it positive. Then returns sOld.
	public static long inverse(long e, long m){
		long sOld = 1;
		long s = 0;
		long tOld = 0;
		long t = 1;
		long remainderOld = e;
		long remainder = m;
		
		while(remainder != 0){
			long quotient = remainderOld/remainder;
			
			//updating the remainder variables
			long tempR = remainderOld;
			remainderOld = remainder;
			remainder = tempR - quotient*remainder;
			
			//updating the s variables
			long tempS = sOld;
			sOld = s;
			s = tempS - quotient*s;
			
			//updating the t variables
			long tempT = tOld;
			tOld = t;
			t = tempT - quotient*t;
		}
		
		if(remainderOld != 1){
			throw new IllegalStateException();
		}

		if(sOld < 0){
			sOld = sOld+m;
		}
		
		return sOld;
	}
	
	//This method is used to find a^b mod m.
	//Goes through a loop. The loop accounts for
	//a number being too large that would otherwise
	//not fit in the long datatype.
	public static long modPow(long a, long b, long m){
		long current = a;
		for(int i = 1; i< b; i++){
			current = (current * a) % m;	
		}
		return current;
	}
	
	//Creates a long[] array and converts a String to
	//type long. Returns the array.
	public static long[] stringToNum(String s){
		long[] list = new long[s.length()];
		for(int i = 0; i<s.length(); i++){
			char letter = s.charAt(i);
			int position = letter - 'A';
			list[i] = position;
		}
		return list;
	}
	
	//Converts an long[] array to a String and
	//returns a String.
	public static String numToString(long[] array){
		String s = "";
		for(int i = 0; i<array.length; i++){
			int position = (int)array[i];
			char letter = alphabet[position];
			s = s + letter;
		}
		return s;
	}
}
