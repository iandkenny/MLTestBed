package org.mltestbed.util.EMD;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.logging.Level;

import org.mltestbed.util.Complex;
import org.mltestbed.util.Detrend;
import org.mltestbed.util.FFT;
import org.mltestbed.util.Log;
import org.mltestbed.util.Util;

public class Tdic
{

	private double[][] c = null;// is the correlation matrix
	private double[][] p = null;// is the indicator for student-test
	private int[] scale = null;// is the time scale
	private double[] tx = null; // is the time axis
	private double[][] f = null;
	private double[][] a = null;
	private double[] allX;
	private double[] allY;

	public Tdic()
	{
	}
	/**
	%EMD  computes Empirical Mode Decomposition
	%
	%
	%   Syntax
	%
	%
	% IMF = EMD(X)
	% IMF = EMD(X,...,'Option_name',Option_value,...)
	% IMF = EMD(X,OPTS)
	% [IMF,ORT,NB_ITERATIONS] = EMD(...)
	%
	%
	%   Description
	%
	%
	% IMF = EMD(X) where X is a real vector computes the Empirical Mode
	% Decomposition [1] of X, resulting in a matrix IMF containing 1 IMF per row, the
	% last one being the residue. The default stopping criterion is the one proposed
	% in [2]:
	%
	%   at each point, mean_amplitude < THRESHOLD2*envelope_amplitude
	%   &
	%   mean of boolean array {(mean_amplitude)/(envelope_amplitude) > THRESHOLD} < TOLERANCE
	%   &
	%   |#zeros-#extrema|<=1
	%
	% where mean_amplitude = abs(envelope_max+envelope_min)/2
	% and envelope_amplitude = abs(envelope_max-envelope_min)/2
	% 
	% IMF = EMD(X) where X is a complex vector computes Bivariate Empirical Mode
	% Decomposition [3] of X, resulting in a matrix IMF containing 1 IMF per row, the
	% last one being the residue. The default stopping criterion is similar to the
	% one proposed in [2]:
	%
	%   at each point, mean_amplitude < THRESHOLD2*envelope_amplitude
	%   &
	%   mean of boolean array {(mean_amplitude)/(envelope_amplitude) > THRESHOLD} < TOLERANCE
	%
	% where mean_amplitude and envelope_amplitude have definitions similar to the
	% real case
	%
	% IMF = EMD(X,...,'Option_name',Option_value,...) sets options Option_name to
	% the specified Option_value (see Options)
	%
	% IMF = EMD(X,OPTS) is equivalent to the above syntax provided OPTS is a struct 
	% object with field names corresponding to option names and field values being the 
	% associated values 
	%
	% [IMF,ORT,NB_ITERATIONS] = EMD(...) returns an index of orthogonality
	%                       ________
	%         _  |IMF(i,:).*IMF(j,:)|
	%   ORT = \ _____________________
	%         /
	%         ¯        || X ||²
	%        i~=j
	%
	% and the number of iterations to extract each mode in NB_ITERATIONS
	%
	%
	%   Options
	%
	%
	%  stopping criterion options:
	%
	% STOP: vector of stopping parameters [THRESHOLD,THRESHOLD2,TOLERANCE]
	% if the input vector's length is less than 3, only the first parameters are
	% set, the remaining ones taking default values.
	% default: [0.05,0.5,0.05]
	%
	% FIX (int): disable the default stopping criterion and do exactly <FIX> 
	% number of sifting iterations for each mode
	%
	% FIX_H (int): disable the default stopping criterion and do <FIX_H> sifting 
	% iterations with |#zeros-#extrema|<=1 to stop [4]
	%
	%  bivariate/complex EMD options:
	%
	% COMPLEX_VERSION: selects the algorithm used for complex EMD ([3])
	% COMPLEX_VERSION = 1: "algorithm 1"
	% COMPLEX_VERSION = 2: "algorithm 2" (default)
	% 
	% NDIRS: number of directions in which envelopes are computed (default 4)
	% rem: the actual number of directions (according to [3]) is 2*NDIRS
	% 
	%  other options:
	%
	% T: sampling times (line vector) (default: 1:length(x))
	%
	% MAXITERATIONS: maximum number of sifting iterations for the computation of each
	% mode (default: 2000)
	%
	% MAXMODES: maximum number of imfs extracted (default: Inf)
	%
	% DISPLAY: if equals to 1 shows sifting steps with pause
	% if equals to 2 shows sifting steps without pause (movie style)
	% rem: display is disabled when the input is complex
	%
	% INTERP: interpolation scheme: 'linear', 'cubic', 'pchip' or 'spline' (default)
	% see interp1 documentation for details
	%
	% MASK: masking signal used to improve the decomposition according to [5]
	%
	%
	%   Examples
	%
	%
	%X = rand(1,512);
	%
	%IMF = emd(X);
	%
	%IMF = emd(X,'STOP',[0.1,0.5,0.05],'MAXITERATIONS',100);
	%
	%T=linspace(0,20,1e3);
	%X = 2*exp(i*T)+exp(3*i*T)+.5*T;
	%IMF = emd(X,'T',T);
	%
	%OPTIONS.DISLPAY = 1;
	%OPTIONS.FIX = 10;
	%OPTIONS.MAXMODES = 3;
	%[IMF,ORT,NBITS] = emd(X,OPTIONS);
	%
	%
	%   References
	%
	%
	% [1] N. E. Huang et al., "The empirical mode decomposition and the
	% Hilbert spectrum for non-linear and non stationary time series analysis",
	% Proc. Royal Soc. London A, Vol. 454, pp. 903-995, 1998
	%
	% [2] G. Rilling, P. Flandrin and P. Gonçalves
	% "On Empirical Mode Decomposition and its algorithms",
	% IEEE-EURASIP Workshop on Nonlinear Signal and Image Processing
	% NSIP-03, Grado (I), June 2003
	%
	% [3] G. Rilling, P. Flandrin, P. Gonçalves and J. M. Lilly.,
	% "Bivariate Empirical Mode Decomposition",
	% Signal Processing Letters (submitted)
	%
	% [4] N. E. Huang et al., "A confidence limit for the Empirical Mode
	% Decomposition and Hilbert spectral analysis",
	% Proc. Royal Soc. London A, Vol. 459, pp. 2317-2345, 2003
	%
	% [5] R. Deering and J. F. Kaiser, "The use of a masking signal to improve 
	% empirical mode decomposition", ICASSP 2005
	%
	%
	% See also
	%  emd_visu (visualization),
	%  emdc, emdc_fix (fast implementations of EMD),
	%  cemdc, cemdc_fix, cemdc2, cemdc2_fix (fast implementations of bivariate EMD),
	%  hhspectrum (Hilbert-Huang spectrum)
	%
	%
	% G. Rilling, last modification: 3.2007
	% gabriel.rilling@ens-lyon.fr
*/

	/*function [imf,ort,nbits] = emd(varargin)

	[x,t,sd,sd2,tol,MODE_COMPLEX,ndirs,display_sifting,sdt,sd2t,r,imf,k,nbit,NbIt,MAXITERATIONS,FIXE,FIXE_H,MAXMODES,INTERP,mask] = init(varargin{:});

	if( display_sifting)
	  fig_h = figure;
	end


	//%main loop : requires at least 3 extrema to proceed
	while ~stop_EMD(r,MODE_COMPLEX,ndirs) && (k < MAXMODES+1 || MAXMODES == 0) && ~any(mask)

	  //% current mode
	  m = r;

	  //% mode at previous iteration
	  mp = m;

	  //%computation of mean and stopping criterion
	  if FIXE
	    [stop_sift,moyenne] = stop_sifting_fixe(t,m,INTERP,MODE_COMPLEX,ndirs);
	  elseif FIXE_H
	    stop_count = 0;
	    [stop_sift,moyenne] = stop_sifting_fixe_h(t,m,INTERP,stop_count,FIXE_H,MODE_COMPLEX,ndirs);
	  else
	    [stop_sift,moyenne] = stop_sifting(m,t,sd,sd2,tol,INTERP,MODE_COMPLEX,ndirs);
	  end

	  //% in case the current mode is so small that machine precision can cause
	  //% spurious extrema to appear
	  if (max(abs(m))) < (1e-10)*(max(abs(x)))
	    if ~stop_sift
	      warning('emd:warning','forced stop of EMD : too small amplitude')
	    else
	      disp('forced stop of EMD : too small amplitude')
	    end
	    break
	  end


	  % sifting loop
	  while ~stop_sift && nbit<MAXITERATIONS

	    if(~MODE_COMPLEX && nbit>MAXITERATIONS/5 && mod(nbit,floor(MAXITERATIONS/10))==0 && ~FIXE && nbit > 100)
	      disp(['mode ',int2str(k),', iteration ',int2str(nbit)])
	      if exist('s','var')
	        disp(['stop parameter mean value : ',num2str(s)])
	      end
	      [im,iM] = extr(m);
	      disp([int2str(sum(m(im) > 0)),' minima > 0; ',int2str(sum(m(iM) < 0)),' maxima < 0.'])
	    end

	    %sifting
	    m = m - moyenne;

	    %computation of mean and stopping criterion
	    if FIXE
	      [stop_sift,moyenne] = stop_sifting_fixe(t,m,INTERP,MODE_COMPLEX,ndirs);
	    elseif FIXE_H
	      [stop_sift,moyenne,stop_count] = stop_sifting_fixe_h(t,m,INTERP,stop_count,FIXE_H,MODE_COMPLEX,ndirs);
	    else
	      [stop_sift,moyenne,s] = stop_sifting(m,t,sd,sd2,tol,INTERP,MODE_COMPLEX,ndirs);
	    end

	    % display
	    if display_sifting && ~MODE_COMPLEX
	      NBSYM = 2;
	      [indmin,indmax] = extr(mp);
	      [tmin,tmax,mmin,mmax] = boundary_conditions(indmin,indmax,t,mp,mp,NBSYM);
	      envminp = interp1(tmin,mmin,t,INTERP);
	      envmaxp = interp1(tmax,mmax,t,INTERP);
	      envmoyp = (envminp+envmaxp)/2;
	      if FIXE || FIXE_H
	        display_emd_fixe(t,m,mp,r,envminp,envmaxp,envmoyp,nbit,k,display_sifting)
	      else
	        sxp=2*(abs(envmoyp))./(abs(envmaxp-envminp));
	        sp = mean(sxp);
	        display_emd(t,m,mp,r,envminp,envmaxp,envmoyp,s,sp,sxp,sdt,sd2t,nbit,k,display_sifting,stop_sift)
	      end
	    end

	    mp = m;
	    nbit=nbit+1;
	    NbIt=NbIt+1;

	    if(nbit==(MAXITERATIONS-1) && ~FIXE && nbit > 100)
	      if exist('s','var')
	        warning('emd:warning',['forced stop of sifting : too many iterations... mode ',int2str(k),'. stop parameter mean value : ',num2str(s)])
	      else
	        warning('emd:warning',['forced stop of sifting : too many iterations... mode ',int2str(k),'.'])
	      end
	    end

	  end % sifting loop
	  imf(k,:) = m;
	  if display_sifting
	    disp(['mode ',int2str(k),' stored'])
	  end
	  nbits(k) = nbit;
	  k = k+1;


	  r = r - m;
	  nbit=0;


	end %main loop

	if any(r) && ~any(mask)
	  imf(k,:) = r;
	end

	ort = io(x,imf);

	if display_sifting
	  close
	end
	end

	%---------------------------------------------------------------------------------------------------
	% tests if there are enough (3) extrema to continue the decomposition
	function stop = stop_EMD(r,MODE_COMPLEX,ndirs)
	if MODE_COMPLEX
	  for k = 1:ndirs
	    phi = (k-1)*pi/ndirs;
	    [indmin,indmax] = extr(real(exp(i*phi)*r));
	    ner(k) = length(indmin) + length(indmax);
	  end
	  stop = any(ner < 3);
	else
	  [indmin,indmax] = extr(r);
	  ner = length(indmin) + length(indmax);
	  stop = ner < 3;
	end
	end

	%---------------------------------------------------------------------------------------------------
	% computes the mean of the envelopes and the mode amplitude estimate
	function [envmoy,nem,nzm,amp] = mean_and_amplitude(m,t,INTERP,MODE_COMPLEX,ndirs)
	NBSYM = 2;
	if MODE_COMPLEX
	  switch MODE_COMPLEX
	    case 1
	      for k = 1:ndirs
	        phi = (k-1)*pi/ndirs;
	        y = real(exp(-i*phi)*m);
	        [indmin,indmax,indzer] = extr(y);
	        nem(k) = length(indmin)+length(indmax);
	        nzm(k) = length(indzer);
	        [tmin,tmax,zmin,zmax] = boundary_conditions(indmin,indmax,t,y,m,NBSYM);
	        envmin(k,:) = interp1(tmin,zmin,t,INTERP);
	        envmax(k,:) = interp1(tmax,zmax,t,INTERP);
	      end
	      envmoy = mean((envmin+envmax)/2,1);
	      if nargout > 3
	        amp = mean(abs(envmax-envmin),1)/2;
	      end
	    case 2
	      for k = 1:ndirs
	        phi = (k-1)*pi/ndirs;
	        y = real(exp(-i*phi)*m);
	        [indmin,indmax,indzer] = extr(y);
	        nem(k) = length(indmin)+length(indmax);
	        nzm(k) = length(indzer);
	        [tmin,tmax,zmin,zmax] = boundary_conditions(indmin,indmax,t,y,y,NBSYM);
	        envmin(k,:) = exp(i*phi)*interp1(tmin,zmin,t,INTERP);
	        envmax(k,:) = exp(i*phi)*interp1(tmax,zmax,t,INTERP);
	      end
	      envmoy = mean((envmin+envmax),1);
	      if nargout > 3
	        amp = mean(abs(envmax-envmin),1)/2;
	      end
	  end
	else
	  [indmin,indmax,indzer] = extr(m);
	  nem = length(indmin)+length(indmax);
	  nzm = length(indzer);
	  [tmin,tmax,mmin,mmax] = boundary_conditions(indmin,indmax,t,m,m,NBSYM);
	  envmin = interp1(tmin,mmin,t,INTERP);
	  envmax = interp1(tmax,mmax,t,INTERP);
	  envmoy = (envmin+envmax)/2;
	  if nargout > 3
	    amp = mean(abs(envmax-envmin),1)/2;
	  end
	end
	end

	%-------------------------------------------------------------------------------
	% default stopping criterion
	function [stop,envmoy,s] = stop_sifting(m,t,sd,sd2,tol,INTERP,MODE_COMPLEX,ndirs)
	try
	  [envmoy,nem,nzm,amp] = mean_and_amplitude(m,t,INTERP,MODE_COMPLEX,ndirs);
	  sx = abs(envmoy)./amp;
	  s = mean(sx);
	  stop = ~((mean(sx > sd) > tol | any(sx > sd2)) & (all(nem > 2)));
	  if ~MODE_COMPLEX
	    stop = stop && ~(abs(nzm-nem)>1);
	  end
	catch
	  stop = 1;
	  envmoy = zeros(1,length(m));
	  s = NaN;
	end
	end

	%-------------------------------------------------------------------------------
	% stopping criterion corresponding to option FIX
	function [stop,moyenne]= stop_sifting_fixe(t,m,INTERP,MODE_COMPLEX,ndirs)
	try
	  moyenne = mean_and_amplitude(m,t,INTERP,MODE_COMPLEX,ndirs);
	  stop = 0;
	catch
	  moyenne = zeros(1,length(m));
	  stop = 1;
	end
	end

	%-------------------------------------------------------------------------------
	% stopping criterion corresponding to option FIX_H
	function [stop,moyenne,stop_count]= stop_sifting_fixe_h(t,m,INTERP,stop_count,FIXE_H,MODE_COMPLEX,ndirs)
	try
	  [moyenne,nem,nzm] = mean_and_amplitude(m,t,INTERP,MODE_COMPLEX,ndirs);
	  if (all(abs(nzm-nem)>1))
	    stop = 0;
	    stop_count = 0;
	  else
	    stop_count = stop_count+1;
	    stop = (stop_count == FIXE_H);
	  end
	catch
	  moyenne = zeros(1,length(m));
	  stop = 1;
	end
	end

	%-------------------------------------------------------------------------------
	% displays the progression of the decomposition with the default stopping criterion
	function display_emd(t,m,mp,r,envmin,envmax,envmoy,s,sb,sx,sdt,sd2t,nbit,k,display_sifting,stop_sift)
	subplot(4,1,1)
	plot(t,mp);hold on;
	plot(t,envmax,'--k');plot(t,envmin,'--k');plot(t,envmoy,'r');
	title(['IMF ',int2str(k),';   iteration ',int2str(nbit),' before sifting']);
	set(gca,'XTick',[])
	hold  off
	subplot(4,1,2)
	plot(t,sx)
	hold on
	plot(t,sdt,'--r')
	plot(t,sd2t,':k')
	title('stop parameter')
	set(gca,'XTick',[])
	hold off
	subplot(4,1,3)
	plot(t,m)
	title(['IMF ',int2str(k),';   iteration ',int2str(nbit),' after sifting']);
	set(gca,'XTick',[])
	subplot(4,1,4);
	plot(t,r-m)
	title('residue');
	disp(['stop parameter mean value : ',num2str(sb),' before sifting and ',num2str(s),' after'])
	if stop_sift
	  disp('last iteration for this mode')
	end
	if display_sifting == 2
	  pause(0.01)
	else
	  pause
	end
	end

	%---------------------------------------------------------------------------------------------------
	% displays the progression of the decomposition with the FIX and FIX_H stopping criteria
	function display_emd_fixe(t,m,mp,r,envmin,envmax,envmoy,nbit,k,display_sifting)
	subplot(3,1,1)
	plot(t,mp);hold on;
	plot(t,envmax,'--k');plot(t,envmin,'--k');plot(t,envmoy,'r');
	title(['IMF ',int2str(k),';   iteration ',int2str(nbit),' before sifting']);
	set(gca,'XTick',[])
	hold  off
	subplot(3,1,2)
	plot(t,m)
	title(['IMF ',int2str(k),';   iteration ',int2str(nbit),' after sifting']);
	set(gca,'XTick',[])
	subplot(3,1,3);
	plot(t,r-m)
	title('residue');
	if display_sifting == 2
	  pause(0.01)
	else
	  pause
	end
	end

	%---------------------------------------------------------------------------------------
	% defines new extrema points to extend the interpolations at the edges of the
	% signal (mainly mirror symmetry)
	function [tmin,tmax,zmin,zmax] = boundary_conditions(indmin,indmax,t,x,z,nbsym)
		
		lx = length(x);
		
		if (length(indmin) + length(indmax) < 3)
			error('not enough extrema')
		end

	    % boundary conditions for interpolations :

		if indmax(1) < indmin(1)
	    	if x(1) > x(indmin(1))
				lmax = fliplr(indmax(2:min(end,nbsym+1)));
				lmin = fliplr(indmin(1:min(end,nbsym)));
				lsym = indmax(1);
			else
				lmax = fliplr(indmax(1:min(end,nbsym)));
				lmin = [fliplr(indmin(1:min(end,nbsym-1))),1];
				lsym = 1;
			end
		else

			if x(1) < x(indmax(1))
				lmax = fliplr(indmax(1:min(end,nbsym)));
				lmin = fliplr(indmin(2:min(end,nbsym+1)));
				lsym = indmin(1);
			else
				lmax = [fliplr(indmax(1:min(end,nbsym-1))),1];
				lmin = fliplr(indmin(1:min(end,nbsym)));
				lsym = 1;
			end
		end
	    
		if indmax(end) < indmin(end)
			if x(end) < x(indmax(end))
				rmax = fliplr(indmax(max(end-nbsym+1,1):end));
				rmin = fliplr(indmin(max(end-nbsym,1):end-1));
				rsym = indmin(end);
			else
				rmax = [lx,fliplr(indmax(max(end-nbsym+2,1):end))];
				rmin = fliplr(indmin(max(end-nbsym+1,1):end));
				rsym = lx;
			end
		else
			if x(end) > x(indmin(end))
				rmax = fliplr(indmax(max(end-nbsym,1):end-1));
				rmin = fliplr(indmin(max(end-nbsym+1,1):end));
				rsym = indmax(end);
			else
				rmax = fliplr(indmax(max(end-nbsym+1,1):end));
				rmin = [lx,fliplr(indmin(max(end-nbsym+2,1):end))];
				rsym = lx;
			end
		end
	    
		tlmin = 2*t(lsym)-t(lmin);
		tlmax = 2*t(lsym)-t(lmax);
		trmin = 2*t(rsym)-t(rmin);
		trmax = 2*t(rsym)-t(rmax);
	    
		% in case symmetrized parts do not extend enough
		if tlmin(1) > t(1) || tlmax(1) > t(1)
			if lsym == indmax(1)
				lmax = fliplr(indmax(1:min(end,nbsym)));
			else
				lmin = fliplr(indmin(1:min(end,nbsym)));
			end
			if lsym == 1
				error('bug')
			end
			lsym = 1;
			tlmin = 2*t(lsym)-t(lmin);
			tlmax = 2*t(lsym)-t(lmax);
		end   
	    
		if trmin(end) < t(lx) || trmax(end) < t(lx)
			if rsym == indmax(end)
				rmax = fliplr(indmax(max(end-nbsym+1,1):end));
			else
				rmin = fliplr(indmin(max(end-nbsym+1,1):end));
			end
		if rsym == lx
			error('bug')
		end
			rsym = lx;
			trmin = 2*t(rsym)-t(rmin);
			trmax = 2*t(rsym)-t(rmax);
		end 
	          
		zlmax =z(lmax); 
		zlmin =z(lmin);
		zrmax =z(rmax); 
		zrmin =z(rmin);
	     
		tmin = [tlmin t(indmin) trmin];
		tmax = [tlmax t(indmax) trmax];
		zmin = [zlmin z(indmin) zrmin];
		zmax = [zlmax z(indmax) zrmax];
	end
	    
	%---------------------------------------------------------------------------------------------------
	%extracts the indices of extrema
	function [indmin, indmax, indzer] = extr(x,t)

	if(nargin==1)
	  t=1:length(x);
	end

	m = length(x);

	if nargout > 2
	  x1=x(1:m-1);
	  x2=x(2:m);
	  indzer = find(x1.*x2<0);

	  if any(x == 0)
	    iz = find( x==0 );
	    indz = [];
	    if any(diff(iz)==1)
	      zer = x == 0;
	      dz = diff([0 zer 0]);
	      debz = find(dz == 1);
	      finz = find(dz == -1)-1;
	      indz = round((debz+finz)/2);
	    else
	      indz = iz;
	    end
	    indzer = sort([indzer indz]);
	  end
	end

	d = diff(x);

	n = length(d);
	d1 = d(1:n-1);
	d2 = d(2:n);
	indmin = find(d1.*d2<0 & d1<0)+1;
	indmax = find(d1.*d2<0 & d1>0)+1;


	% when two or more successive points have the same value we consider only one extremum in the middle of the constant area
	% (only works if the signal is uniformly sampled)

	if any(d==0)

	  imax = [];
	  imin = [];

	  bad = (d==0);
	  dd = diff([0 bad 0]);
	  debs = find(dd == 1);
	  fins = find(dd == -1);
	  if debs(1) == 1
	    if length(debs) > 1
	      debs = debs(2:end);
	      fins = fins(2:end);
	    else
	      debs = [];
	      fins = [];
	    end
	  end
	  if length(debs) > 0
	    if fins(end) == m
	      if length(debs) > 1
	        debs = debs(1:(end-1));
	        fins = fins(1:(end-1));

	      else
	        debs = [];
	        fins = [];
	      end
	    end
	  end
	  lc = length(debs);
	  if lc > 0
	    for k = 1:lc
	      if d(debs(k)-1) > 0
	        if d(fins(k)) < 0
	          imax = [imax round((fins(k)+debs(k))/2)];
	        end
	      else
	        if d(fins(k)) > 0
	          imin = [imin round((fins(k)+debs(k))/2)];
	        end
	      end
	    end
	  end

	  if length(imax) > 0
	    indmax = sort([indmax imax]);
	  end

	  if length(imin) > 0
	    indmin = sort([indmin imin]);
	  end

	end
	end

	%---------------------------------------------------------------------------------------------------

	function ort = io(x,imf)
	% ort = IO(x,imf) computes the index of orthogonality
	%
	% inputs : - x    : analyzed signal
	%          - imf  : empirical mode decomposition

	n = size(imf,1);

	s = 0;

	for i = 1:n
	  for j =1:n
	    if i~=j
	      s = s + abs(sum(imf(i,:).*conj(imf(j,:)))/sum(x.^2));
	    end
	  end
	end

	ort = 0.5*s;
	end
	%---------------------------------------------------------------------------------------------------

	function [x,t,sd,sd2,tol,MODE_COMPLEX,ndirs,display_sifting,sdt,sd2t,r,imf,k,nbit,NbIt,MAXITERATIONS,FIXE,FIXE_H,MAXMODES,INTERP,mask] = init(varargin)

	x = varargin{1};
	if nargin == 2
	  if isstruct(varargin{2})
	    inopts = varargin{2};
	  else
	    error('when using 2 arguments the first one is the analyzed signal X and the second one is a struct object describing the options')
	  end
	elseif nargin > 2
	  try
	    inopts = struct(varargin{2:end});
	  catch
	    error('bad argument syntax')
	  end
	end

	% default for stopping
	defstop = [0.05,0.5,0.05];

	opt_fields = {'t','stop','display','maxiterations','fix','maxmodes','interp','fix_h','mask','ndirs','complex_version'};

	defopts.stop = defstop;
	defopts.display = 0;
	defopts.t = 1:max(size(x));
	defopts.maxiterations = 2000;
	defopts.fix = 0;
	defopts.maxmodes = 0;
	defopts.interp = 'spline';
	defopts.fix_h = 0;
	defopts.mask = 0;
	defopts.ndirs = 4;
	defopts.complex_version = 2;

	opts = defopts;



	if(nargin==1)
	  inopts = defopts;
	elseif nargin == 0
	  error('not enough arguments')
	end


	names = fieldnames(inopts);
	for nom = names'
	  if ~any(strcmpi(char(nom), opt_fields))
	    error(['bad option field name: ',char(nom)])
	  end
	  if ~isempty(eval(['inopts.',char(nom)])) % empty values are discarded
	    eval(['opts.',lower(char(nom)),' = inopts.',char(nom),';'])
	  end
	end

	t = opts.t;
	stop = opts.stop;
	display_sifting = opts.display;
	MAXITERATIONS = opts.maxiterations;
	FIXE = opts.fix;
	MAXMODES = opts.maxmodes;
	INTERP = opts.interp;
	FIXE_H = opts.fix_h;
	mask = opts.mask;
	ndirs = opts.ndirs;
	complex_version = opts.complex_version;

	if ~isvector(x)
	  error('X must have only one row or one column')
	end

	if size(x,1) > 1
	  x = x.';
	end

	if ~isvector(t)
	  error('option field T must have only one row or one column')
	end

	if ~isreal(t)
	  error('time instants T must be a real vector')
	end

	if size(t,1) > 1
	  t = t';
	end

	if (length(t)~=length(x))
	  error('X and option field T must have the same length')
	end

	if ~isvector(stop) || length(stop) > 3
	  error('option field STOP must have only one row or one column of max three elements')
	end

	if ~all(isfinite(x))
	  error('baseData elements must be finite')
	end

	if size(stop,1) > 1
	  stop = stop';
	end

	L = length(stop);
	if L < 3
	  stop(3)=defstop(3);
	end

	if L < 2
	  stop(2)=defstop(2);
	end


	if ~ischar(INTERP) || ~any(strcmpi(INTERP,{'linear','cubic','spline'}))
	  error('INTERP field must be ''linear'', ''cubic'', ''pchip'' or ''spline''')
	end

	%special procedure when a masking signal is specified
	if any(mask)
	  if ~isvector(mask) || length(mask) ~= length(x)
	    error('masking signal must have the same dimension as the analyzed signal X')
	  end

	  if size(mask,1) > 1
	    mask = mask.';
	  end
	  opts.mask = 0;
	  imf1 = emd(x+mask,opts);
	  imf2 = emd(x-mask,opts);
	  if size(imf1,1) ~= size(imf2,1)
	    warning('emd:warning',['the two sets of IMFs have different sizes: ',int2str(size(imf1,1)),' and ',int2str(size(imf2,1)),' IMFs.'])
	  end
	  S1 = size(imf1,1);
	  S2 = size(imf2,1);
	  if S1 ~= S2
	    if S1 < S2
	      tmp = imf1;
	      imf1 = imf2;
	      imf2 = tmp;
	    end
	    imf2(max(S1,S2),1) = 0;
	  end
	  imf = (imf1+imf2)/2;

	end


	sd = stop(1);
	sd2 = stop(2);
	tol = stop(3);

	lx = length(x);

	sdt = sd*ones(1,lx);
	sd2t = sd2*ones(1,lx);

	if FIXE
	  MAXITERATIONS = FIXE;
	  if FIXE_H
	    error('cannot use both ''FIX'' and ''FIX_H'' modes')
	  end
	end

	MODE_COMPLEX = ~isreal(x)*complex_version;
	if MODE_COMPLEX && complex_version ~= 1 && complex_version ~= 2
	  error('COMPLEX_VERSION parameter must equal 1 or 2')
	end


	% number of extrema and zero-crossings in residual
	ner = lx;
	nzr = lx;

	r = x;

	if ~any(mask) % if a masking signal is specified "imf" already exists at this stage
	  imf = [];
	end
	k = 1;

	% iterations counter for extraction of 1 mode
	nbit=0;

	% total iterations counter
	NbIt=0;
	end
	%---------------------------------------------------------------------------------------------------
*/
	private double betacf_sc(Double arg0, Double arg1, Double arg2)
	{
		/**
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */

		/* declarations */
		double a, b, x, out1 = Double.NaN;
		int m, m2, Ny, i, j, maxit;
		double c, d, h, qab, qam, qap, stp, tmp, eps, fpmin, aa, del;
//		double[] coef;

		try
		{
			/* check input */
//if (nrhs!=3)     mexErrMsgTxt("Three input!");
			if (arg0 == null)
				throw new Exception("The first input is empty!");
			if (arg1 == null)
				throw new Exception("The second input is empty!");
			if (arg2 == null)
				throw new Exception("The thrid input is empty!");

			/* get input baseData */
			a = arg0.doubleValue();// mxGetPr(prhs[0]);
			b = arg1.doubleValue();// mxGetPr(prhs[1]);
			x = arg2.doubleValue();// mxGetPr(prhs[2]);

			maxit = 100;
			eps = 2.220446049250313e-016;
			fpmin = 2.225073858507201e-308 / eps;
			qab = a + b;

			qap = a + 1.0;
			qam = a - 1.0;
			c = 1.0;
			d = 1.0 - qab * x / qap;
			if (Math.abs(d) < fpmin)
				d = fpmin;

			d = 1.0 / d;

			h = d;

//plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//out1=mxGetPr(plhs[0]);

			for (m = 0; m < maxit; m++)
			{
				m2 = 2 * m;

				aa = m * (b - m) * x / ((qam + m2) * (a + m2));

				d = 1.0 + aa * d;
				if (Math.abs(d) < fpmin)
					d = fpmin;

				c = 1.0 + aa / c;

				if (Math.abs(c) < fpmin)
					c = fpmin;

				d = 1.0 / d;

				h = h * d * c;

				aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));

				d = 1.0 + aa * d;
				if (Math.abs(d) < fpmin)
					d = fpmin;

				c = 1.0 + aa / c;

				if (Math.abs(c) < fpmin)
					c = fpmin;
				d = 1.0 / d;

				del = d * c;
				h = h * del;

				// could be coded better for Java, as coded is consistent with
				// the original C
				if (Math.abs(del - 1.0) <= eps)
				{
					out1 = h;
					return out1;
				}

				out1 = h;
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
		}
		// must have default return in Java
		return out1;
	}
	private double betai_s(double a, double b, double x)
	{
		/**
		 * Ported from the original MATLAB code,written by Yongxiang HUANG
		 * 04-2012, by Ian Kenny 04-2022
		 */
//%use nrtype; use nrutil, only : assert
//%use nr, only : betacf,gammln
		double bt;
		if (x == 0.0 || x == 1.0)
			bt = 0.0;
		else // %factors in front of the continued fraction
			bt = btc(a, b, x);
		double prob;
		if (x < (a + 1.0d) / (a + b + 2.0d)) // %use continued fraction
												// directly.
			prob = bt * betacf_sc(a, b, x) / a;
		else // %use continued fraction after making the
			prob = 1.0d - bt * betacf_sc(b, a, 1.0d - x) / b; // %symmetry
																// transformation.
		return prob;
	}
	private double btc(Double arg0, Double arg1, Double arg2)
	{
		/**
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */

		/* declarations */
		double x, a, b, out1 = Double.NaN;
//		int dof, Nx, Ny, i, j;
		double stp, tmp, xx, tmp2;
		double[] coef;

		try
		{
			/* check input */
//    if (nrhs!=3)     mexErrMsgTxt("Three input!");
			if (arg0 == null)
				throw new Exception("The first input is empty!");
			if (arg1 == null)
				throw new Exception("The second input is empty!");
			if (arg2 == null)
				throw new Exception("The thrid input is empty!");

			/* get input baseData */
			a = arg0.doubleValue();// mxGetPr(prhs[0]);
			b = arg1.doubleValue();// mxGetPr(prhs[1]);
			x = arg2.doubleValue();// mxGetPr(prhs[2]);

			stp = 2.5066282746310005;

			coef = new double[6];// (double *)malloc(6*sizeof(double));;
			coef[0] = 76.18009172947146;
			coef[1] = -86.50532032941677;
			coef[2] = 24.01409824083091;
			coef[3] = -1.231739572450155;
			coef[4] = 0.1208650973866179e-2;
			coef[5] = -0.5395239384953e-5;

//    plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//    out1=mxGetPr(plhs[0]);

			xx = a + b;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp2 = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);

			xx = a;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);
			tmp2 = tmp2 - tmp;

			xx = b;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);
			tmp2 = tmp2 - tmp;

			tmp2 = tmp2 + a * Math.log(x) + b * Math.log(1.0 - x);
			out1 = Math.exp(tmp2);
			/* free allocated memory */

//    free(coef);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//	e.printStackTrace();
		}
		return out1;
	}

	public double[][] getCorrelationMatrix()
	{
		return c;
	}

	public int[] getScaleAxis()
	{
		return scale;
	}
	public double[][] getStudentTest()
	{
		return p;
	}

	public double[] getTimeAxis()
	{
		return tx;
	}

	private double[] myxcorrc(double[] x, double[] y, int df)
	{
		/**
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */
		/* declarations */
		int pSizeX, pSizeY;

		double[] out1 = new double[3];
		int i;// ,j;
		double tmp, stdx, stdy, tiny;

		/* check input */
		try
		{
//		if (nrhs!=3)    throw new Exception("Three input!");
			if (x == null || x.length == 0)
				throw new Exception("The first is empty!");
			if (y == null || y.length == 0)
				throw new Exception("The second is empty!");
			if (df <= 0)
				throw new Exception("The third is not valid!");
			/* get input baseData */
//    x=mxGetPr(prhs[0]);
//    y=mxGetPr(prhs[1]);
//    df=mxGetPr(prhs[2]);

			tiny = 1.0e-20;

			pSizeX = x.length; // mxGetNumberOfElements(prhs[0]);
			pSizeY = y.length;// mxGetNumberOfElements(prhs[1]);
			if (pSizeX != pSizeY)
				throw new Exception("Two inputs should have the same length!");

//		plhs[0]=mxCreateDoubleMatrix(1,3,mxREAL);
			out1 = new double[pSizeX];// mxGetPr(plhs[0]);

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i];
			tmp = tmp / pSizeX;
			for (i = 0; i < pSizeX; i++)
				x[i] = -tmp + x[i];

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + y[i];
			tmp = tmp / pSizeX;
			for (i = 0; i < pSizeX; i++)
				y[i] = -tmp + y[i];

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i] * x[i];
			tmp = tmp / pSizeX;
			stdx = Math.sqrt(tmp) + tiny;

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + y[i] * y[i];
			tmp = tmp / pSizeX;
			stdy = Math.sqrt(tmp) + tiny;

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i] * y[i];
			tmp = tmp / pSizeX;
			out1[0] = tmp / stdx / stdy;

			out1[1] = 0.5e0 * Math.log(
					((1.0e0 + out1[0]) + tiny) / ((1.0e0 - out1[0]) + tiny));

			out1[2] = out1[0] * Math.sqrt(df / (((1.0e0 - out1[0]) + tiny)
					* ((1.0e0 + out1[0]) + tiny)));
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//		e.printStackTrace();
		}
		return out1;
	}

	private double studentc(Double arg0, Integer arg1)
	{
		/**
		 * Ported from the original MATLAB code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */


		/* declarations */
//    int  pSizeI, pSizeB;
//    
//    int i,j,k,kk,i0,j0,k0,Np;
//    
		double[] a, b, c, d, e1, f, g, h, i1, j1;
		double t, out1 = Double.NaN;
		int df;
		double nonsense, v, pos, x;
		/*
		 * instantaneous frequency, instantaneous amplitude, resolution of
		 * amplitude resolution frequency
		 */

		try
		{
			/* check input */
//    if (nrhs!=2)     mexErrMsgTxt("You have to input three parameters!");
			if (arg0 == null)
				throw new Exception("t is empty!");
			if (arg1 == null)
				throw new Exception("df is empty!");

			/* get input baseData */
			t = arg0.doubleValue();// mxGetPr(prhs[0]);
			df = arg1.intValue();// mxGetPr(prhs[1]);

			nonsense = 99999.99;

			a = new double[5];// (double *)malloc(5*sizeof(double));;
			a[0] = 0.09979441;
			a[1] = -0.581821;
			a[2] = 1.390993;
			a[3] = -1.222452;
			a[4] = 2.151185;

			b = new double[2];// (double *)malloc(2*sizeof(double));;
			b[0] = 5.537409;
			b[1] = 11.42343;

			c = new double[5];// (double *)malloc(5*sizeof(double));;
			c[0] = 0.04431742;
			c[1] = -0.2206018;
			c[2] = -0.03317253;
			c[3] = 5.679969;
			c[4] = -12.96519;

			d = new double[2];// (double *)malloc(2*sizeof(double));;
			d[0] = 5.166733;
			d[1] = 13.49862;

			e1 = new double[5];// (double *)malloc(5*sizeof(double));;
			e1[0] = 0.009694901;
			e1[1] = -0.1408854;
			e1[2] = 1.88993;
			e1[3] = -12.75532;
			e1[4] = 25.77532;

			f = new double[2];// (double *)malloc(2*sizeof(double));;
			f[0] = 4.233736;
			f[1] = 14.3963;

			g = new double[5];// (double *)malloc(5*sizeof(double));;
			g[0] = -9.187228e-5;
			g[1] = 0.03789901;
			g[2] = -1.280346;
			g[3] = 9.249528;
			g[4] = -19.08115;

			h = new double[2];// (double *)malloc(2*sizeof(double));;
			h[0] = 2.777816;
			h[1] = 16.46132;

			i1 = new double[5];// (double *)malloc(5*sizeof(double));;
			i1[0] = 5.79602e-4;
			i1[1] = -0.02763334;
			i1[2] = 0.4517029;
			i1[3] = -2.657697;
			i1[4] = 5.127212;

			j1 = new double[2];// (double *)malloc(2*sizeof(double));;

			j1[0] = 0.5657187;
			j1[1] = 21.83269;

//		plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//		out1=new double[1];//mxGetPr(plhs[0]);

			if (df <= 4.0)
				out1 = nonsense;

			else
			{
				v = 1.0 / df;
				if (t >= 0)
					pos = 1.0;
				else
					pos = 0;

				if (t < 0)
					t = -t;
				x = (1.0 + t * (((a[0]
						+ v * (a[1] + v * (a[2] + v * (a[3] + v * a[4]))))
						/ (1.0 - v * (b[0] - v * b[1])))
						+ t * (((c[0] + v
								* (c[1] + v * (c[2] + v * (c[3] + v * c[4]))))
								/ (1.0 - v * (d[0] - v * d[1])))
								+ t * (((e1[0] + v * (e1[1] + v
										* (e1[2] + v * (e1[3] + v * e1[4]))))
										/ (1.0 - v * (f[0] - v * f[1])))
										+ t * (((g[0] + v * (g[1] + v * (g[2]
												+ v * (g[3] + v * g[4]))))
												/ (1.0 - v * (h[0] - v * h[1])))
												+ t * ((i1[0] + v * (i1[1] + v
														* (i1[2] + v * (i1[3]
																+ v * i1[4]))))
														/ (1.0 - v * (j1[0] - v
																* j1[1]))))))));
				x = 0.5 * Math.pow(x, -8);
				if (pos == 1.0)
					out1 = x;
				else
					out1 = 1.0 - x;
			}

//    free(a);
//    free(b);
//    free(c);
//    free(d);
//    free(e1);
//    free(g);
//    free(h);
//    free(i1);
//    free(j1);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//		e.printStackTrace();
		}
		return out1;
	}
	public String toXML(String tagName, double[][] matrix)
	{
		StringBuilder xml = new StringBuilder();
		String replaceAll = tagName.replaceAll("<", "").replaceAll(">", "");
 		xml.append("<+" + replaceAll + ">");
		NumberFormat formatter = new DecimalFormat(
				"##################0.0##############################");
		int rows = matrix.length;
		int cols = matrix[0].length;
		for (int i = 0; i < rows; i++)
		{
			xml.append("\n\t<Row>");
			for (int j = 0; j < cols; j++)
			{
				System.out.println("rows = " + rows + "cols= " + cols + "i= "
						+ i + " j= " + j+ " xml size="+xml.length());
				xml.append("\n\t\t<Cell>" + formatter.format(matrix[i][j])
						+ "\n\t\t</Cell>");
			}
			xml.append("\n\t</Row>");
		}
		xml.append("\n</Matrix>");

		xml.append("\n<" + replaceAll + ">");
		return xml.toString();
	}
	public double[] maxlocalperiod(double[][] x, double[][] y)
	{
		/**
		 * Ported from the original MATLAB code,written by Yongxiang HUANG
		 * 04-2012, by Ian Kenny 04-2022
		 * 
		 * % This function is to calculate the maximum local period from two
		 * given IMF
		 * % modes
		 */
		double[][] p1 = localperiod(x, 1);
		double[][] p2 = localperiod(y, 1);
//		pp=max([p1;p2]);
		double[] maxp1 = Util.maxPerColumn(p1);
		double[] maxp2 = Util.maxPerColumn(p2);
		double[] ret = new double[maxp1.length];

		for (int i = 0; i < maxp1.length; i++)
			ret[i] = (int) ((maxp1[i] > maxp2[i]) ? maxp1[i] : maxp2[i]);

		return ret;
	}
	private double[][] localperiod(double[][] data, Integer dt)
	{
		/**
		 * Ported from the original MATLAB code,written by Yongxiang HUANG
		 * 04-2012, by Ian Kenny 04-2022
		 *
		 * % The function PAZ generates a period using zero-crossing method
		 * % applied to baseData(n,k), where n specifies the length of time series,
		 * % and k is the number of IMFs.
		 * % Non MATLAB Library routine used in the function is:
		 * FINDCRITICALPOINTS.
		 * %
		 * % Calling sequence-
		 * % p=faz(baseData)
		 * %
		 * % Input-
		 * % baseData - 2-D matrix of IMF components
		 * % dt - time increment per point
		 * % Output-
		 * % p - 2-D matrix f(n,k) that specifies frequency
		 * %
		 * %
		 * %----- Get dimensions
		 */
		if (dt == null)
			dt = new Integer(1);
//			[f,a]=fazoi(baseData,dt);
		fazoi(data, dt);
//		    p=1./f; // element wise division
		double[][] p = Util.inverse(f);

		return p;
	}

	private void quickcriticalpoints(double[] in)
	{
		int npts = in.length;
		double[] allx = new double[npts], ally = new double[npts];
		for (int i = 0; i < npts / 2; i++)
			allx[i] = ally[i] = 0;

		boolean wasSmaller = in[2] > in[1];
		boolean isSmaller, isPositive;

		boolean wasPositive = in[1] > 0;
		int constStart = 0;
		if (in[1] == in[0])
			constStart = 1;
		int idx = 0; // 1; 0 based array indexing

		for (int i = 1; i < npts - 1; i++) // i starts at 1 not 2
		{
			isSmaller = in[i + 1] > in[i];
			isPositive = in[i] > 0;
			boolean isConstant = in[i + 1] == in[i];

			if (wasPositive != isPositive)
			{
				// % check that the previous point was an extremum
				// % if (cp->n)
				// % assert(cp->y[cp->n-1] != 0);
				// if below added to prevent array out of bounds, in order to
				// preserve the existing functioning of the code… Rather than
				// change the logic
				if (idx > allx.length - 1)
				{
					allx = Arrays.copyOf(allx, idx + 1);
					ally = Arrays.copyOf(ally, idx + 1);
				}
				allx[idx] = i - 0.5;
				ally[idx] = 0;
				idx = idx + 1;
				wasPositive = isPositive;
			}

			// % find extrema
			double diff;
			if (constStart == 0 && !isConstant)
			{
				if (wasSmaller != isSmaller)
				{
					diff = in[i + 1] - in[i - 1];
					double den = (2 * in[i] - in[i - 1] - in[i + 1]);
					// if below added to prevent array out of bounds, in order
					// to preserve the existing functioning of the code… Rather
					// than change the logic
					if (idx > allx.length - 1)
					{
						allx = Arrays.copyOf(allx, idx + 1);
						ally = Arrays.copyOf(ally, idx + 1);
					}
					allx[idx] = i;
					ally[idx] = in[i];
					idx = idx + 1;
					wasSmaller = isSmaller;
				}
			} else if (constStart != 0 && isConstant)
				continue; // % Don't touch wasSmaller!
			else if (constStart != 0 && !isConstant)
			{
				// % We haven't touched wasSmaller since the constant zone
				// started!
				if (wasSmaller != isSmaller)
				{
					// if below added to prevent array out of bounds, in order
					// to preserve the existing functioning of the code… Rather
					// than change the logic
					if (idx > allx.length - 1)
					{
						allx = Arrays.copyOf(allx, idx + 1);
						ally = Arrays.copyOf(ally, idx + 1);
					}

					// % put an "extremum" at the middle point
					allx[idx] = 0.5 * (constStart + i);
					ally[idx] = in[i];
					idx++;
					wasSmaller = isSmaller;
				} // % otherwise it's just a spurious constant value along a
					// sloped region.
				constStart = 0;
			} else if (constStart == 0 && isConstant)
				constStart = i;
		}

		// % check for zero crossing on last point
		isPositive = in[in.length - 1] > 0;
		if (wasPositive != isPositive)
		{
			// if below added to prevent array out of bounds, in order to
			// preserve the existing functioning of the code… Rather than change
			// the logic
			if (idx > allx.length - 1)
			{
				allx = Arrays.copyOf(allx, idx + 1);
				ally = Arrays.copyOf(ally, idx + 1);
			}

			allx[idx] = in.length + in[in.length - 1]
					/ (in[in.length - 1] - in[in.length - 2]);
			ally[idx] = 0;
			idx++;
		}

		// % clear unused points
//			allx(idx:end) = [];
//			ally(idx:end) = [];
		for (int j = idx; j < ally.length; j++)
		{
			allx[j] = Double.NaN;
			ally[j] = Double.NaN;
		}
		allX = allx;
		allY = ally;
//			if nargout < 2
//			    allx(2,:)=ally(1,:);
//			end
	}
	private void fazoi(double[][] data, int dt)
	{
		/**
		 * Ported from the original MATLAB code Ian Kenny 05-2022
		 * 
		 * 
		 * % The function FAZ generates a frequency and amplitude using
		 * zero-crossing method % applied to baseData(n,k), where n specifies the
		 * length of time series, % and k is the number of IMFs. % Non MATLAB
		 * Library routine used in the function is: FINDCRITICALPOINTS. % %
		 * Calling sequence- % [f,a]=faz(baseData,dt) % % Input- % baseData - 2-D matrix
		 * of IMF components % dt - time increment per point % Output- % f - 2-D
		 * matrix f(n,k) that specifies frequency % a - 2-D matrix a(n,k) that
		 * specifies amplitude % % Used by- % FA % See also- % ZFAPANLS, which
		 * in addition to frequency and amplitude, outputs % other fields.
		 * 
		 * % Kenneth Arnold (NASA GSFC) Summer 2003, Modified
		 */
		// %----- Get dimensions
//			[nPoints, nIMF] = size(baseData);
		int nPoints = data.length;
		int nIMF = data[0].length;
		// %----- Flip baseData if necessary
		boolean flipped = false;
		if (nPoints < nIMF)
		{
			// %----- Flip baseData set
			data = Util.transpose(data);
			// [nPoints, nIMF] = size(baseData);
			nPoints = data.length;
			nIMF = data[0].length;
			flipped = true;
		}

		// %----- Inverse dt
//			idt = 1./dt;
		int idt = 1 / dt;
		// %----- Preallocate arrays
		f = Util.zeros(nPoints, nIMF);
		a = f.clone();
		// %----- Process each IMF
//			for c=1:nIMF
		for (int c = 0; c < nIMF; c++)
		{
			// %----- Find all critical points
			double[] series = new double[data.length];
			for (int i = 0; i < data.length; i++)
				series[i] = data[i][c];

			quickcriticalpoints(series);
			int nCrit = allX.length;

			if (nCrit == 1)
			{
				// % One critical point. If it's an extremum, we can make a
				// guess.
				// % This is based on the premise that the wave in question
				// might be
				// % an intermittent wave, in which case the first and last
				// points
				// % would be zero crossings but are not marked as such.
				if (allY[1] == 0)
					continue;
//			        f(:,c) = idt / nPoints;
//					a(:,c) = allY(1);
				for (int j = 0; j < f.length; j++)
					f[j][c] = idt / nPoints;
				for (int i = 0; i < a.length; i++)
					a[i][c] = allY[1];

			} else if (nCrit < 1)
			{
				// %----- Too few critical points; keep looping
				continue;
			}

			// %----- Initialize previous calculated frequencies
			double f2prev1 = Double.NaN;
			double f4prev1 = Double.NaN;
			double f4prev2 = Double.NaN;
			double f4prev3 = Double.NaN;

			// %----- Initialize previous calculated amplitudes
			double a2prev1 = Double.NaN;
			double a4prev1 = Double.NaN;
			double a4prev2 = Double.NaN;
			double a4prev3 = 3.0;

			for (int i = 0; i < nCrit - 1; i++)
			{
				// %----- Estimate current frequency
				double cx = allX[i];
				double f1 = idt / (allX[i + 1] - cx);
				double a1 = 4
						* Math.max(Math.abs(allY[i]), Math.abs(allY[i + 1]));
				int npt = 4;
				double ftotal = f1;
				double atotal = a1;

				double f2cur;
				double[] range;
				double[] ext;
				double a2cur;
				if (i + 2 < nCrit)
				{
					f2cur = idt / (allX[i + 2] - cx);
//			            range = allY(i:i+2);
					range = Arrays.copyOfRange(allY, i, i + 2);

//			            ext = range(range~=0);
					ext = new double[range.length];
					for (int j = 0; j < ext.length; j++)
						if (range[j] != 0)
							ext[j] = range[j];
					a2cur = 2 * Util.absmean(ext);
					npt = npt + 2;
					ftotal = ftotal + f2cur;
					atotal = atotal + a2cur;
				} else
				{
					f2cur = Double.NaN;
					a2cur = Double.NaN;
				}

				double f4cur, a4cur;
				if (i + 4 < nCrit)
				{
					f4cur = idt / (allX[i + 4] - cx);
//			            range = allY(i:i+4);
					range = Arrays.copyOfRange(allY, i, i + 4);

//			            ext = range(range~=0);
					ext = new double[range.length];
					for (int j = 0; j < ext.length; j++)
						if (ext[j] != 0)
							range[j] = ext[j];

					a4cur = Util.absmean(ext);
					npt = npt + 1;
					ftotal = ftotal + f4cur;
					atotal = atotal + a4cur;
				} else
				{
					f4cur = Double.NaN;
					a4cur = Double.NaN;
				}

				// %----- Add previous points if they are valid
				if (!Util.isnan(f2prev1))
				{
					npt = npt + 2;
					ftotal = ftotal + f2prev1;
					atotal = atotal + a2prev1;
				}
				if (!Util.isnan(f4prev1))
				{
					npt = npt + 1;
					ftotal = ftotal + f4prev1;
					atotal = atotal + a4prev1;
				}
				if (!Util.isnan(f4prev2))
				{
					npt = npt + 1;
					ftotal = ftotal + f4prev2;
					atotal = atotal + a4prev2;
				}
				if (!Util.isnan(f4prev3))
				{
					npt = npt + 1;
					ftotal = ftotal + f4prev3;
					atotal = atotal + a4prev3;
				}

//			        f(ceil(allX(i)):floor(allX(i+1)),c) = ftotal/npt;
				for (int j = (int) Math.ceil(allX[i]); j < Math
						.floor(allX[i + 1]); j++)
					f[j][c] = ftotal / npt;

//			        a(ceil(allX(i)):floor(allX(i+1)),c) = atotal/npt;
				for (int j = (int) Math.ceil(allX[i]); j < Math
						.floor(allX[i + 1]); j++)
					a[j][c] = ftotal / npt;
				f2prev1 = f2cur;
				f4prev3 = f4prev2;
				f4prev2 = f4prev1;
				f4prev1 = f4cur;

				a2prev1 = a2cur;
				a4prev3 = a4prev2;
				a4prev2 = a4prev1;
				a4prev1 = a4cur;
			}

			// %----- Fill in ends
//			    f(1:ceil(allX(1))-1,c) = f(ceil(allX(1)),c);
			for (int j = 0; j < Math.ceil(allX[1]) - 1; j++)
				f[j][c] = f[(int) Math.ceil(allX[1])][c];
//			    f(floor(allX(nCrit))+1:nPoints,c) = f(floor(allX(nCrit)),c);
			for (int j = (int) (Math.floor(allX[nCrit - 1])
					+ 1); j < nPoints; j++)
				f[j][c] = f[(int) Math.floor(allX[nCrit - 1])][c];
//			    a(1:ceil(allX(1))-1,c) = a(ceil(allX(1)),c);
			for (int j = 0; j < Math.ceil(allX[1]) - 1; j++)
				a[j][c] = a[(int) Math.ceil(allX[1])][c];
//			    a(floor(allX(nCrit))+1:nPoints,c) = a(floor(allX(nCrit)),c);
			for (int j = (int) (Math.floor(allX[nCrit - 1])
					+ 1); j < nPoints; j++)
				a[j][c] = a[(int) Math.floor(allX[nCrit - 1])][c];

			// %----- Flip again if baseData was flipped at the beginning
			if (flipped)
			{
//			    f=f';
				f = Util.transpose(f);
//			    a=a';
				a = Util.transpose(a);
			}
		}
	}

	public void tdic(double[] x, double[] y, double[] ifz, Integer ntime,
			Integer nct, Boolean it)
	{
		/**
		 * Ported from the MATLAB code by Ian Kenny 04-2022 Original comment
		 * below:
		 * 
		 * % [c,p,tx,scale]= tdic(x,y,ifz,ntime,nct,it)
		 * % Input
		 * % x is the time series of the first variable 
		 * % y is the time series of the second variable
		 * % ifz is the instantaneous period provided by the zero-crossing method
		 * % it is the output of the function maxlocalperiod
		 * % ntime is the maximum window size, the default value is half of the length 
		 * % of the baseData
		 * % nct is the maximum size of moving time window
		 * % - it: definition of degree of freedom
		 * % - 0: means default, i.e.,d.o.f. = n-2 
		 * % - 1: means how many cycles included in the region
		 * % Output
		 * % c is the correlation matrix 
		 * % p is the indicator for student-test 
		 * % tx is the time axis 
		 * % scale is the time scale
		 * %
		 * %
		 * % [c,p,tx,scale]=tdicnew(x,y,pp);
		 *
		 * % To show the result: surf(tx,scale,(c.*p))
		 * 
		 * % Written by Yongxiang HUANG 04-2012
		 * 
		 */
		/*
		 * %check input
		 * 
		 */
		double[] out1;
		double r;
		double t;
		double z;

		int Nx = x.length;
		int Ny = y.length;
		try
		{
			if (Nx != Ny)
				throw new Exception(
						"Length of the first two inputs should be the same.");

			if (ntime == null && nct == null && it == null)
			{
				ntime = Nx;
				nct = (int) Math.floor(ntime / 2);
				it = false;
			}

			if (ntime > Nx)
				ntime = Nx;

			int ncct;
			if (nct > 0.5 * ntime)
				ncct = (int) Math.floor(0.5 * ntime);
			else
				ncct = nct;

			if (it == null)
				it = false;

			c = new double[ncct][ntime];// *nan;//%initial the output
			p = new double[ncct][ntime];// *nan;//%initial the output
			for (int j = 0; j < c.length; j++)
				for (int k = 0; k < c[0].length; k++)
				{
					c[j][k] = Double.NaN;
					p[j][k] = Double.NaN;
				}

//			p=c; // looking at the code below we need two arrays
			tx = new double[ntime];
			for (int k = 0; k < ntime; k++)
				tx[k] = Double.NaN;// zeros(1,ntime)*nan;

			int df = 0;
			for (int j = 0; j < ntime; j++)
			{
				int d = (int) Math.max(Math.floor(ifz[j] * 0.5e0), 1);
 				if (d <= nct.intValue())
				{

					for (int i = d; i < ncct; i++)
					{
						if (j - i < 1
								|| j + i > ntime
								|| j + i <= j - i)
							continue;

						int ndf = 0;
						if (it == false)
							df = ntime - 2;
						else
							ndf = 0;

						for (int ii = j - i + 1; ii < j + i - 1; ii++)
						{
							if ((x[ii - 1] <= x[ii]) && (x[ii] >= x[ii + 1]))
								ndf = ndf + 1;
						}
						if (ndf == 0)
							continue;

						df = ndf;

//			            %%%%%
						out1 = new double[3];
						int len = (j - 1) - (j + 1);
						double[] subx = new double[len];
						double[] suby = new double[len];
						System.arraycopy(x, j-1, subx, 0, len);
						System.arraycopy(y, j-1, suby, 0, len);

						out1 = myxcorrc(subx, suby, df);// %calculate the
														// local
														// correlation
						r = out1[1];
						z = out1[2];
						t = out1[3];

						double prob = betai_s(0.5e0 * df, 0.5e0,
								df / Math.pow(df + t, 22));// % student's t
															// probability.
//			            %%%%%

						c[i][j] = r;
						p[i][j] = prob;
						tx[j] = j;
						if (it) // % we do not use this part
							t = Math.sqrt(1.0 - r * r);
						if (t == 0.0)
							t = t + 0.0000010;
						t = r * Math.sqrt(df) / t;
						p[i][j] = studentc(t, df);
					}
				}
			}
//			p(abs(p)>0.5)=Double.NaN; //% check the student test
//			p(abs(p)<0.5)=1;			
			for (int j = 0; j < p.length; j++)
				for (int k = 0; k < p[0].length; k++)
					p[j][k] = (Math.abs(p[j][k]) > 0.5)
							? Double.NaN
							: (Math.abs(p[j][k]) < 0.5) ? 1 : p[j][k];
//			c(c>1)=nan; //%check the correlation matrix
//			c(c==0)=nan;
			for (int j = 0; j < c.length; j++)
				for (int k = 0; k < c[0].length; k++)
					c[j][k] = (c[j][k] > 1 || c[j][k] == 0)
							? Double.NaN
							: c[j][k];
			scale = new int[p.length];
			for (int k = 0; k < p.length; k++)
				scale[k] = k;
//			scale=1:size(p,1);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}

	public double[] meanf(double[][] imf, int fq)
	{
		/**
		 * Ported from the MATLAB code by Ian Kenny 05-2022 Original comment
		 * below:
		 * 
		 * %[mf]=meanf(imf,fq)
		 * %*********************************************************************
		 * % This function is to estimate the mean frequency of each imf based
		 * on the 
		 * % Fourier transform 
		 * % Input 
		 * % imf(n,k) n is the length of f,k is each IMF frequency 
		 * % fq is the sampling frequency: the default value equal 1
		 * %
		 * % Output 
		 * % mf is the mean frequency definited in NE Huang 1998 
		 * % 
		 * % Written by Yongxiang Huang 5/8/2005
		 * %******************************************************************
		 * 
		 */
//			if nargin==1
//			    fq=1;
//			}
//			if iscell(imf)
//			    error('You should call the function imf2freq to calculate the mean frequency !')
//			}
//			if (size(imf,1)<size(imf,2)
		if (imf.length < imf[0].length)
		{
//			   imf=imf';
			imf = Util.transpose(imf);
		}
		int N = imf.length;
		
		double[][] ax = imf.clone();
		double[] mf= new double[ax[0].length];
//			for i=1:size(ax,2)-1
		for (int i = 0; i < ax[0].length; i++)
		{

			double[] x = new double[ax.length];
			for (int j = 0; j < ax.length; j++)
//			  x=ax(:,i);
				x[j] = ax[j][i];
			x = Detrend.detrend(x); // % modified by Yongxiang Huang 14/01/2008
			Complex[] cx = new Complex[x.length];
			for (int k = 0; k < x.length; k++)
				cx[k] = new Complex(x[k], 0);
			Complex[] cd = FFT.dft(cx); // % Fourier transform
//			  pdd=d.*conj(d)/length(d);//% get the spectrum
			Complex[] pdd = new Complex[cd.length];
			for (int k = 0; k < cd.length; k++)
				pdd[k] = cd[k].times(cd[k].conjugate())
						.divides(new Complex(cd.length, 0));
			N = pdd.length;
			int halfN = (int) Math.floor(N / 2); //make the implicit rounding down explicit 
			double[] f = new double[halfN];
//			  f=fq*(0:N/2)/N;//%get the real frequency
			for (int k = 0; k < halfN; k++)
				f[k] = (double) (fq * k) / N;
//			  mf(i)=sum(f'.*pdd(1:length(f)))/sum(pdd(1:length(f))); //% get the mean
			Complex sum1 = new Complex(0,0);
			Complex sum2 = new Complex(0,0);
			for (int k = 0; k < f.length; k++)
			{
				sum1 = sum1.plus(pdd[k].scale(f[k]));
				sum2= sum2.plus(pdd[k]);
			}
			mf[i]=sum1.divides(sum2).abs();						// % frequency
		}
		return mf;
	}

	/**
	 * 
	 */
	public String toXML(String tagName, int[] vector)
	{

		StringBuilder xml = new StringBuilder();
		String replaceAll = tagName.replaceAll("<", "").replaceAll(">", "");
		xml.append("\n<" + replaceAll + ">");
		
		if (vector != null)
		{
			int cols = vector.length;
			xml.append("<Vector>");
			for (int j = 0; j < cols; j++)
			{
				System.out.println("cols= " + cols + " j= " + j);
				xml.append("<Cell>"+vector[j] + "</Cell>");
			}
			xml.append("</Vector>");
		}
		xml.append("\n<" + replaceAll + ">");
		return xml.toString();
	}
	public String toXML(String tagName, double[] vector)
	{
		StringBuilder xml = new StringBuilder();
		String replaceAll = tagName.replaceAll("<", "").replaceAll(">", "");
		xml.append("<" + replaceAll + ">");
		NumberFormat formatter = new DecimalFormat(
				"##################0.0##############################");
		
		if (vector != null)
		{
			int cols = vector.length;
			xml.append("<Vector>");
			for (int j = 0; j < cols; j++)
			{
				System.out.println("cols= " + cols + " j= " + j);
				xml.append("<Cell>" + formatter.format(vector[j]) + "</Cell>");
			}
			xml.append("</Vector>");
		}
		xml.append("\n<" + replaceAll + ">");
		return xml.toString();
	}
}