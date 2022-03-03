/*
 * lyap.c - attractor finder using lyapunov exponents
 *
 * version 1.0
 * John Holder / November 12, 2002
 *
 * This code was written after reading Paul Bourke's code and then rewriting
 * things to work the way I thought they should.  You can see his at:
 * http://astronomy.swin.edu.au/~pbourke/fractals/lyapunov/gen.c
 *
 * the Lyapunov exponent is computed by:
 *
 *                 1   N       dx(n+1)
 * lamba =  lim   --- SUM  ln( ------- )
 *         N->inf  N  n=1       dx(n)
 *
 * (SUM is the capital sigma)
 *
 * Consider two close points at time step n, x(n) and x(n)+dx(n). 
 * At the next time step they will have diverged to x(n+1) and 
 * x(n+1)+dx(n+1). The Lyapunov exponent (traditionally represented
 * by a lambda) captures the average rate of convergence or divergence.
 * The exponent tells us the rate that we lose information about the
 * initial conditions.
 *
 * We don't actually have to take the derivative of our function, because
 * there is a principle of calculus that says that the difference in the 
 * solutions after one iteration divided by the difference before the 
 * iteration, provided the difference is small, is equal to the derivative
 * of the equation for the map.  This makes computation much easier.
 * (See "Strange Attractors, p.14, by J.C.Sprott)
 *
 * lambda > 0 : the system is chaotic, yet deterministic.  The magnitude
 *              of lambda tells us how sensitive to initial conditions the
 *              system is.  i.e., chaotic systems with lambda very close to
 *              zero are still boring to look at.  This program reports
 *              "stable" for this type of chaotic attractor.  (where lambda
 *              is smaller than 1e-4)
 * 
 * lambda = 0 : the system is stable.  (but boring to look at...)
 *
 * lambda < 0 : the system attracts to either a stable periodic orbit or a
 *              fixed point.  This program notices the difference between 
 *              fixed points and periodic attractors.
 *
 * NOTES
 *  - At the moment, the images are in greyscale PGM and are x-y projections.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <string.h>

/* Windows porting garbage for MS VisualC */
#if defined(WIN32)
#include <float.h>
#define isnan        _isnan
#define isinf        !_finite
#define fmin         __min
#define fmax         __max
#define srand48      srand
#define drand48()   ((double)rand()/(double)RAND_MAX)
#endif

/* I like using TRUE and FALSE */
#if !defined(TRUE)
#define TRUE 1
#endif
#if !defined(FALSE)
#define FALSE 0
#endif

/* points per attractor */
/* YOU WILL NEED 3*MAXITERATIONS*sizeof(double) bytes of memory to compute an image,
   i.e., for 1000000 points, you need 12MB of RAM.  Memory is cheap... get more if
   this is a problem... */
int MAXITERATIONS = 10000000;
/* after LYAPCHECK iterations, an early check of the lyap 
   exponent is done to see if we can bail */
int LYAPCHECK = 10000;
/* after this number of iterations, the attractor is considered "settled" and we
   start computing lyapunov exponents. */
int LYAPSETTLE = 1000;
/* the max number of tries to find chaotic attractors */
int NEXAMPLES = 10000;
/* after TARGET chaotic attractors are found, we quit. */
int TARGET = 10;
double *x,*y,*z;

int SaveAttractor(int,double *,double);

int main(int argc,char **argv)
{
   double ax[5];
   long i,n;
   int drawit;
   int found=0;
   long secs, secs2;
   double d0,dd,dx,dy,dz,lyapunov;
   double xe,ye,ze,xenew=0,yenew=0,zenew=0;
   int inf=0,pnt=0,stb=0,per=0,cha=0;

   x = calloc( MAXITERATIONS+1, sizeof(double) );
   y = calloc( MAXITERATIONS+1, sizeof(double) );
   z = calloc( MAXITERATIONS+1, sizeof(double) );
   time( &secs );
   srand48( secs );

   for( n=0; n<NEXAMPLES; n++ ) {

      /* Initialize things */
      for( i=0; i<5; i++ ) {
         ax[i] = 6.0 * (drand48() - 0.5); /* range of initial values: +- 3 */
      }
      lyapunov = 0.0;

      /* Calculate the attractor */
      drawit = TRUE;
      /* start at a random point from +- 0.5 */
      x[0] = drand48() - 0.5;
      y[0] = drand48() - 0.5;
      z[0] = drand48() - 0.5;
      /* set up a x+epsilon point for lyaping */
      xe = x[0] + (drand48() - 0.5) / 1000.0;
      ye = y[0] + (drand48() - 0.5) / 1000.0;
      ze = y[0] + (drand48() - 0.5) / 1000.0;
      /* compute the deltas */
      dx = x[0] - xe;
      dy = y[0] - ye;
      dz = z[0] - ze;
      d0 = sqrt( dx*dx + dy*dy + dz*dz );

      for( i=1; i<MAXITERATIONS; i++ ) {

         /* Calculate next term */
         x[i] = sin( ax[0]*y[i-1] ) - z[i-1]*cos( ax[1]*x[i-1] );
         y[i] = z[i-1]*sin( ax[2]*x[i-1] ) - cos( ax[3]*y[i-1] );
         z[i] = ax[4]*sin( x[i-1] );

         xenew = sin( ax[0]*ye ) - ze*cos( ax[1]*xe );
         yenew = ze*sin( ax[2]*xe ) - cos( ax[3]*ye );
         zenew = ax[4]*sin( xe );

         /* Does the series tend to infinity? */
         if( x[i] < -1e10 || y[i] < -1e10 || z[i] < -1e10 || 
             x[i] >  1e10 || y[i] >  1e10 || z[i] >  1e10    )  
         {
            drawit = FALSE;
            inf++;
            break;
         }

         /* Does the series tend to a point? */
         if( fabs( x[i] - x[i-1] ) < 1e-10 && 
             fabs( y[i] - y[i-1] ) < 1e-10 && 
             fabs( z[i] - z[i-1] ) < 1e-10 ) 
         {
            drawit = FALSE;
            pnt++;
            break;
         }

         /* Calculate the lyapunov exponents after settling */
         if( i > LYAPSETTLE ) {
            dx = x[i] - xenew;
            dy = y[i] - yenew;
            dz = z[i] - zenew;
            dd = sqrt( dx*dx + dy*dy + dz*dz );
            lyapunov += log(fabs(dd / d0));

            /* readjust the x+dx in the direction of the current iter to be within
               epsilon (d0) of x (and so on for each coordinate) */
            xe = x[i] + d0 * dx / dd;
            ye = y[i] + d0 * dy / dd;
            ze = z[i] + d0 * dz / dd;
         }

         /* at some point, see if we can bail out early. */
         if( LYAPCHECK == i )
         {
            if( fabs(lyapunov)/i < 1e-4 || lyapunov < 0.0 ) 
               break;
         }
      }

      lyapunov /= i;
      /* Classify the series according to lyapunov */
      if (drawit) {
         printf("%8d ",n);
         if (fabs(lyapunov) < 1e-4) {
            printf("stable ");
            stb++;
            drawit = FALSE;
         } else if (lyapunov < 0.0) {
            printf("periodic %g ",lyapunov);
            per++;
            drawit = FALSE; 
         } else {
            cha++;
            printf("chaotic %g ",lyapunov); 
         }
         printf("\n");
      }

      /* Save the image */
      if (drawit) 
      {
         SaveAttractor(n,ax,lyapunov);
         found++;
      }
      if( found == TARGET )
         break;
   }
  
   time(&secs2);
   printf("Results (%d seconds)\n",secs2-secs);
   printf("  Infinite: %5d  (%2.1f%%)\n",inf, inf*100.0/(n+1.0) ); 
   printf("  Point   : %5d  (%2.1f%%)\n",pnt, pnt*100.0/(n+1.0) );
   printf("  Stable  : %5d  (%2.1f%%)\n",stb, stb*100.0/(n+1.0) );
   printf("  Periodic: %5d  (%2.1f%%)\n",per, per*100.0/(n+1.0) );
   printf("  Chaotic : %5d  (%2.1f%%)\n",cha, cha*100.0/(n+1.0) );

}

/* saves in the PGM (greyscale) format */
/* since my main program that reads PGM is dumb (LViewPro), I can't put 
   comments in or use P5 binary mode for a little compression.  grrr!
   I'll probably use a better format in the future... 
   Just went with what's easy */
int SaveAttractor(int n,double *a, double lyap)
{
   double xmin,xmax,ymin,ymax,zmin,zmax;
   char fname[128] = {0};
   int i,ix,iy;
   int width = 500, height = 500;
   int len=0;
   long loc, wh, wh1;
   unsigned char *image = NULL;
   FILE *fptr = NULL;

   wh = width*height;
   wh1 = (width+1)*(height+1);

   xmin =  1e32;
   xmax = -1e32;
   ymin =  1e32;
   ymax = -1e32;
   zmin =  1e32;
   zmax = -1e32;
   for (i=100;i<MAXITERATIONS;i++) {
      if( x[i] > xmax ) xmax = x[i];
      if( y[i] > ymax ) ymax = y[i];
      if( z[i] > zmax ) zmax = z[i];
      if( x[i] < xmin ) xmin = x[i];
      if( y[i] < ymin ) ymin = y[i];
      if( z[i] < zmin ) zmin = z[i];
   }


   /* Save the parameters */
   sprintf(fname,"%05d.txt",n); 
   if ((fptr = fopen(fname,"w")) == NULL) {
      fprintf(stderr,"Couldn't open file '%s' for writing.\n",fname);
      return(FALSE);
   }
   fprintf(fptr,"%g %g %g %g\n",xmin,ymin,xmax,ymax);
   for (i=0;i<5;i++)
      fprintf(fptr,"%g\n",a[i]);
   fprintf(fptr,"%g\n",lyap);
   fclose(fptr);

	/* Save the image */
   sprintf(fname,"%05d.pgm",n);

   image = malloc( wh1*sizeof(char) );

   if( image == NULL ) {
      fprintf(stderr,"Couldn't alloc memory for image '%s'.\n",fname);
      return(FALSE);
   }

   memset( image, 0, wh1*sizeof(char) );

   if ((fptr = fopen(fname,"w")) == NULL) {
      fprintf(stderr,"Couldn't open file '%s' for writing.\n",fname);
      return(FALSE);
   }
   fprintf(fptr,"P2\n%d %d\n",width, height);
   fprintf(fptr,"255\n");

   for (i=100;i<MAXITERATIONS;i++) {
      ix = (int)(width * (x[i] - xmin) / (xmax - xmin));
      iy = (int)(height * (y[i] - ymin) / (ymax - ymin));
      loc = iy*width + ix;
      if( loc >=0 && loc < wh1 ) 
      {
         if( image[ loc ] == 0)
            image[ loc ] = 200;
         else {
            if( image[ loc ] < 255)
            image[ loc ]++;
         }
      }
   }
   for (i=0; i<wh1; i++)
   {
      fprintf(fptr,"%d ",(unsigned char)image[ i ]); 
      len+=4;
      if(len > 66) {
         fprintf(fptr,"\n");
         len=0;
      }
   }
   fclose(fptr);

   return TRUE;   
}

