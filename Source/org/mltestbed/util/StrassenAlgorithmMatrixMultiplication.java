package org.mltestbed.util;

//Java Program to Implement Strassen Algorithm

//Class Strassen matrix multiplication
public class StrassenAlgorithmMatrixMultiplication {

	// Method 1
	// Function to multiply matrices
	public double[][] multiply(double[][] A, double[][] B)
	{
		// Order of matrix
		int n = A.length;

		// Creating a 2D square matrix with size n
		// n is input from the user
		double[][] R = new double[n][n];

		// Base case
		// If there is only single element
		if (n == 1)

			// Returning the simple multiplication of
			// two elements in matrices
			R[0][0] = A[0][0] * B[0][0];

		// Matrix
		else {
			// Step 1: Dividing Matrix into parts
			// by storing sub-parts to variables
			double[][] A11 = new double[n / 2][n / 2];
			double[][] A12 = new double[n / 2][n / 2];
			double[][] A21 = new double[n / 2][n / 2];
			double[][] A22 = new double[n / 2][n / 2];
			double[][] B11 = new double[n / 2][n / 2];
			double[][] B12 = new double[n / 2][n / 2];
			double[][] B21 = new double[n / 2][n / 2];
			double[][] B22 = new double[n / 2][n / 2];

			// Step 2: Dividing matrix A into 4 halves
			split(A, A11, 0, 0);
			split(A, A12, 0, n / 2);
			split(A, A21, n / 2, 0);
			split(A, A22, n / 2, n / 2);

			// Step 2: Dividing matrix B into 4 halves
			split(B, B11, 0, 0);
			split(B, B12, 0, n / 2);
			split(B, B21, n / 2, 0);
			split(B, B22, n / 2, n / 2);

			// Using Formulas as described in algorithm

			// M1:=(A1+A3)×(B1+B2)
			double[][] M1
				= multiply(add(A11, A22), add(B11, B22));
		
			// M2:=(A2+A4)×(B3+B4)
			double[][] M2 = multiply(add(A21, A22), B11);
		
			// M3:=(A1−A4)×(B1+A4)
			double[][] M3 = multiply(A11, sub(B12, B22));
		
			// M4:=A1×(B2−B4)
			double[][] M4 = multiply(A22, sub(B21, B11));
		
			// M5:=(A3+A4)×(B1)
			double[][] M5 = multiply(add(A11, A12), B22);
		
			// M6:=(A1+A2)×(B4)
			double[][] M6
				= multiply(sub(A21, A11), add(B11, B12));
		
			// M7:=A4×(B3−B1)
			double[][] M7
				= multiply(sub(A12, A22), add(B21, B22));

			// P:=M2+M3−M6−M7
			double[][] C11 = add(sub(add(M1, M4), M5), M7);
		
			// Q:=M4+M6
			double[][] C12 = add(M3, M5);
		
			// R:=M5+M7
			double[][] C21 = add(M2, M4);
		
			// S:=M1−M3−M4−M5
			double[][] C22 = add(sub(add(M1, M3), M2), M6);

			// Step 3: Join 4 halves into one result matrix
			join(C11, R, 0, 0);
			join(C12, R, 0, n / 2);
			join(C21, R, n / 2, 0);
			join(C22, R, n / 2, n / 2);
		}

		// Step 4: Return result
		return R;
	}

	// Method 2
	// Function to subtract two matrices
	public double[][] sub(double[][] A, double[][] B)
	{
		//
		int n = A.length;

		//
		double[][] C = new double[n][n];

		// Iterating over elements of 2D matrix
		// using nested for loops

		// Outer loop for rows
		for (int i = 0; i < n; i++)

			// Inner loop for columns
			for (int j = 0; j < n; j++)

				// Subtracting corresponding elements
				// from matrices
				C[i][j] = A[i][j] - B[i][j];

		// Returning the resultant matrix
		return C;
	}

	// Method 3
	// Function to add two matrices
	public double[][] add(double[][] A, double[][] B)
	{

		//
		int n = A.length;

		// Creating a 2D square matrix
		double[][] C = new double[n][n];

		// Iterating over elements of 2D matrix
		// using nested for loops

		// Outer loop for rows
		for (int i = 0; i < n; i++)

			// Inner loop for columns
			for (int j = 0; j < n; j++)

				// Adding corresponding elements
				// of matrices
				C[i][j] = A[i][j] + B[i][j];

		// Returning the resultant matrix
		return C;
	}

	// Method 4
	// Function to split parent matrix
	// into child matrices
	public void split(double[][] P, double[][] C, int iB, int jB)
	{
		// Iterating over elements of 2D matrix
		// using nested for loops

		// Outer loop for rows
		for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)

			// Inner loop for columns
			for (int j1 = 0, j2 = jB; j1 < C.length;
				j1++, j2++)

				C[i1][j1] = P[i2][j2];
	}

	// Method 5
	// Function to join child matrices
	// into (to) parent matrix
	public void join(double[][] C, double[][] P, int iB, int jB)

	{
		// Iterating over elements of 2D matrix
		// using nested for loops

		// Outer loop for rows
		for (int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)

			// Inner loop for columns
			for (int j1 = 0, j2 = jB; j1 < C.length;
				j1++, j2++)

				P[i2][j2] = C[i1][j1];
	}

	// Method 5
	// Main driver method
	public static void main(String[] args)
	{
		// Display message
		System.out.println(
			"Strassen Multiplication Algorithm Implementation For Matrix Multiplication :\n");

		// Create an object of Strassen class
		// in he main function
		StrassenAlgorithmMatrixMultiplication s = new StrassenAlgorithmMatrixMultiplication();

		// Size of matrix
		// Considering size as 4 in order to illustrate
		int N = 4;

		// Matrix A
		// Custom input to matrix
		double[][] A = { { 1, 2, 3, 4 },
					{ 4, 3, 0, 1 },
					{ 5, 6, 1, 1 },
					{ 0, 2, 5, 6 } };

		// Matrix B
		// Custom input to matrix
		double[][] B = { { 1, 0, 5, 1 },
					{ 1, 2, 0, 2 },
					{ 0, 3, 2, 3 },
					{ 1, 2, 1, 2 } };

		// Matrix C computations

		// Matrix C calling method to get Result
		double[][] C = s.multiply(A, B);

		// Display message
		System.out.println(
			"\nProduct of matrices A and B : ");

		// Iterating over elements of 2D matrix
		// using nested for loops

		// Outer loop for rows
		for (int i = 0; i < N; i++) {
			// Inner loop for columns
			for (int j = 0; j < N; j++)

				// Printing elements of resultant matrix
				// with whitespaces in between
				System.out.print(C[i][j] + " ");

			// New line once the all elements
			// are printed for specific row
			System.out.println();
		}
	}
}
