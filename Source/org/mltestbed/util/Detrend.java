package org.mltestbed.util;

public class Detrend
{
	public static double[] detrend(double[] sig)
	{
		double[] res = new double[sig.length];
		double[] x = new double[sig.length];
		for (int i = 0; i < x.length; i++)
		{
			x[i] = i;
		}
		double mean_x = mean(x);
		double molecule = 0.0;
		double sum_pow_x = 0.0;
		double sum_x = 0.0;
		for (int i = 0; i < sig.length; i++)
		{
			molecule += (sig[i] * (x[i] - mean_x));
			sum_pow_x += (x[i] * x[i]);
			sum_x += x[i];
		}
		double denomintor = sum_pow_x - sum_x * sum_x / x.length;
		double w = molecule / denomintor;
		double b = 0.0;
		double temp = 0.0;
		for (int i = 0; i < sig.length; i++)
		{
			temp += (sig[i] - w * x[i]);
		}
		b = temp / sig.length;
		for (int i = 0; i < sig.length; i++)
		{
			res[i] = sig[i] - (w * i + b);
		}
		return res;

	}

	private static double mean(double[] data)
	{
		double sum = 0.0;
		for (int i = 0; i < data.length; i++)
		{
			sum += data[i];
		}
		return sum / data.length;
	}
	/**
	 * we dont require the extra validation code 
	function detrend(x,varargin)
	{
				//%DETR} Remove a polynomial tr}.
				//%   Y = DETR}(X) removes the best straight-line fit linear tr} from the
				//%   baseData in vector X and returns the residual in vector Y.  If X is a
				//%   matrix, DETR} removes the tr} from each column of the matrix.
				//%
				//%   Y = DETR}(X,N) removes a polynomial tr} with degree N. N = 1 by
				//%   default. Setting N = 0 is equivalent to using the 'constant' option and
				//%   setting N = 1 is equivalent to using the 'linear' option.
				//%
				//%   Y = DETR}(X,N,BP) removes a continuous, piecewise polynomial tr}
				//%   according to the break points specified in BP. BP can be a vector of
				//%   sample point values or a logical vector of length equal to the number
				//%   of sample points, where true indicates the position of a break point.
				//%
				//%   Y = DETR}(___,NANFLAG) specifies how NaN (Not-A-Number) values are
				//%   treated. The default is 'includenan':
				//%      'includenan' - NaNs are included when calculating the polynomial
				//%                     tr}. If NaNs are present, then the tr} and the
				//%                     corresponding output will be vectors of NaNs.
				//%
				//%      'omitnan'    - NaN values are omitted when calculating the tr}. 
				//%                     NaN values in the input produce corresponding NaN 
				//%                     values in the output.
				//%
				//%   Y = DETR}(___,'Continuous',TF) specifies whether the piecewise
				//%   polynomial tr} is required to be continuous. TF must be one of the
				//%   following:
				//%      true         - (default) the fitted tr} is continuous everywhere
				//%      false        - the fitted tr} is not required to be continuous
				//%
				//%   Y = DETR}(___,'SamplePoints',S) also specifies the sample points S
				//%   associated with the baseData in X. S must be a floating-point or duration
				//%   vector. S must be sorted and contain unique points. You can use S to
				//%   specify time stamps for the baseData. By default, DETR} assumes the baseData
				//%   is sampled uniformly at points S = [1 2 3 ... ].
				//%
				//%   Examples:
				//%
				//%      //% Remove a continuous, piecewise linear tr}
				//%      sig = [0 1 -2 1 0 1 -2 1 0];      //% signal with no linear tr}
				//%      tr} = [0 1 2 3 4 3 2 1 0];      //% two-segment linear tr}
				//%      x = sig+tr};                    //% signal with added tr}
				//%      y = detr}(x,1,5)                //% break point at 5th element
				//%
				//%      //% Remove a nonlinear tr}
				//%      sig = repmat(sig,1,2);
				//%      tr} = ((1:18)/18).^3;
				//%      x = sig+tr};
				//%      y = detr}(x,3);
				//%      plot(sig); hold on
				//%      plot(y)
				//%
				//%      //% Remove discontinuous linear tr} with sample points
				//%      t = -10:10;
				//%      x = t.^3 + 6*t.^2 + 4*t + 3;
				//%      bp = 0;
				//%      y = detr}(x,1,bp,'SamplePoints',t,'Continuous',false);
				//%      plot(t,x,t,y,t,x-y,':k')
				//%      leg}('Input Data','Detr}ed Data','Tr}','Location','northwest') 
				//%
				//%   See also TR}DECOMP, MEAN, ISCHANGE, POLYFIT, POLYVAL

				//%   Copyright 1984-2021 The MathWorks, Inc.

				//% Parse inputs
				[x,polyDeg,bp,s,continuity,sizeX,N,isrowx,isNDx,omitnan] = parseInputs(x, varargin{:});
				if omitnan
				    nanMask = isnan(x);
				    hasNan = any(nanMask,'all');
				    if hasNan
				        y = x;
				        y(:) = NaN;
				        if iscolumn(x)
				            bpNoNans = trimBp(bp,s(~nanMask));
				            y(~nanMask) = detr}Internal(x(~nanMask),polyDeg,bpNoNans,s(~nanMask),continuity,nnz(~nanMask));
				        else
				            columnHasNan = any(nanMask,1);
				            if any(~columnHasNan)
				                bp = trimBp(bp,s);
				                y(:,~columnHasNan) = detr}Internal(x(:,~columnHasNan),polyDeg,bp,s,continuity,N);
				            }
				            columnInd = find(columnHasNan);
				            for ii = columnInd
				                bpNoNans = trimBp(bp,s(~nanMask(:,ii)));
				                y(~nanMask(:,ii),ii) = detr}Internal(x(~nanMask(:,ii),ii),...
				                    polyDeg,bpNoNans,s(~nanMask(:,ii)),continuity,nnz(~nanMask(:,ii)));
				            }
				        }
				    else
				        bp = trimBp(bp,s);
				        y = detr}Internal(x,polyDeg,bp,s,continuity,N);
				    }
				else
				    bp = trimBp(bp,s);
				    y = detr}Internal(x,polyDeg,bp,s,continuity,N);
				}


				if isrowx
				    y = y.';
				elseif isNDx
				    y = reshape(y,sizeX);
				}

				}

				function y = detr}Internal(x,polyDeg,bp,s,continuity,N)

				lbp = length(bp);

				//% Apply method
				if continuity
				    if polyDeg == 0
				        //% Continuous constant subtracts the mean of the first segment
				        [~,begSeg] = min(abs(s-bp(1)));
				        if lbp == 1
				            }Seg = numel(s);
				        else
				            [~,}Seg] = min(abs(s - bp(2)));
				        }
				        segMean = mean(x(begSeg:}Seg,:),1);
				        y = x - segMean;
				    else
				        //% Continuous, piecewise polynomial tr}
				        
				        //% Normalize to avoid numerical issues
				        if isempty(s)
				            a = s;
				            scaleS = s;
				        else
				            scaleS = s(});
				            if scaleS == 0
				                a = s;
				            else
				                a = s./scaleS;
				            }
				        }
				        
				        //% Build regressor
				        b = a - (bp./scaleS)';
				        b = max(b,0);
				        W = b(:).^(polyDeg:-1:1);
				        W = [reshape(W,N,[]), ones(N,1)];
				        x1 = full(x);
				        W = cast(W,'like',x1);
				        
				        //% Solve least squares problem p = W\x1
				        [p, rankW] = matlab.internal.math.leastSquaresFit(W,x1);

				        if size(W,1) < size(W,2) || rankW < size(W,2)
				            warning(message('MATLAB:detr}:PolyNotUnique'));
				        }
				        //% Remove best fit
				        y = x - cast(W*p,'like',x);
				    }
				else
				    y = zeros(size(x),'like',x);
				    segments = sum(s >= bp',2);
				    notUniquePoly = false;
				    x1 = full(x);
				    for k = 1:lbp
				        
				        segidx = segments == k;
				        
				        if polyDeg == 0 || nnz(segidx) == 1
				            //% Remove mean from each segment
				            y(segidx,:) = x(segidx,:) - mean(x(segidx,:),1);
				        else
				            //% Normalize before fitting polynomial
				            a = s(segidx);
				            [std_a, mean_a] = std(a);
				            a = (a - mean_a)./std_a;
				            
				            //% Construct the Vandermonde matrix
				            V = [a.^(polyDeg:-1:1), ones(nnz(segidx),1)];
				            V = cast(V,'like',x1);
				            
				            //% Solve least squares problem tr = V\x1
				            [tr, rankV] = matlab.internal.math.leastSquaresFit(V,x1(segidx,:));

				            if size(V,1) < size(V,2) || rankV < size(V,2)
				                notUniquePoly = true;
				            }
				            
				            //% Remove best fit
				            y(segidx,:) = x(segidx,:) - V*tr;
				        }
				    }
				    if notUniquePoly
				        warning(message('MATLAB:detr}:PolyNotUnique'));
				    }
				}
				}
				//%--------------------------------------------------------------------------
				function [x,polyDeg,bp,s,continuity,sizeX,N,isrowx,isNDx,omitNan] = parseInputs(x, varargin)
				//% Parse inputs

				if ~isfloat(x)
				    error(message('MATLAB:detr}:InvalidFirstInput'));
				}

				sizeX = size(x);
				isrowx = isrow(x);
				isNDx = ~ismatrix(x);
				if isrowx
				    x = x(:);   //% If a row, turn into column vector
				elseif ~ismatrix(x)
				    x = reshape(x,size(x,1),[]); //% If ND, turn into a matrix
				}

				N = size(x,1);

				//% Set default values
				bp = '';
				continuity = true;
				s = [];
				omitNan = false;

				if nargin < 2 || matlab.internal.math.checkInputName(varargin{1},{'Continuous'},4) || ...
				        matlab.internal.math.checkInputName(varargin{1},{'SamplePoints'},1)
				    //% linear method + no break points
				    polyDeg = 1;
				    [continuity,s] = parseNV(1,nargin,continuity,s,x,varargin{:});
				else
				    //% varargin default
				    polyDeg = varargin{1};
				    indStart = 2;
				    
				    //% parse degree n
				    if ~isscalar(polyDeg) && ~ischar(polyDeg)
				        error(message('MATLAB:detr}:InvalidTr}InputType'));
				    elseif islogical(polyDeg)
				        polyDeg = double(polyDeg);
				    elseif ischar(polyDeg) || isstring(polyDeg)
				        if strncmpi(polyDeg,'constant',max(4,strlength(polyDeg)))
				            polyDeg = 0;
				        elseif strncmpi(polyDeg,'linear',max(1,strlength(polyDeg)))
				            polyDeg = 1;
				        else
				            //% Assume NV pair
				            polyDeg = 1;
				            indStart = 1;
				        }
				    elseif ~isnumeric(polyDeg) || ~isreal(polyDeg) || polyDeg < 0 || mod(polyDeg,1) ~= 0
				        error(message('MATLAB:detr}:InvalidTr}InputType'));
				    }
				    
				    //% Parse break points bp
				    if nargin > 2 && ~(ischar(varargin{indStart}) || isstring(varargin{indStart}))
				        bp = varargin{indStart};
				        if isempty(bp) && isnumeric(bp)
				            bp = ''; //%Empty numeric breakpoints are stored as characters until being validated
				        }
				        indStart = indStart+1;
				    }
				    
				    if indStart<nargin && (ischar(varargin{indStart}) || isstring(varargin{indStart}))
				        if matlab.internal.math.checkInputName(varargin{indStart},{'omitnan'},1)
				            omitNan = true;
				            indStart = indStart+1;
				        elseif matlab.internal.math.checkInputName(varargin{indStart},{'includenan'},1)
				            indStart = indStart+1;
				        }
				    }
				    
				    //% Parse name-value pairs
				    [continuity,s] = parseNV(indStart,nargin,continuity,s,x,varargin{:});
				}

				//% Check class of bp now that we have s
				if ~ischar(bp)
				    if isempty(s)
				        if ~(isnumeric(bp) || islogical(bp))  || ~isreal(bp) || ...
				                ~(isvector(bp) || isempty(bp)) || issparse(bp)
				            error(message('MATLAB:detr}:BreakpointsInvalid'));
				        }
				    else
				        if (isnumeric(bp) && ~isreal(bp)) || ...
				                ~(isvector(bp) || isempty(bp)) || issparse(bp) || ...
				                ~(islogical(bp) || (isnumeric(bp) && isnumeric(s))) && ...
				                ~(isequal(class(bp), class(s)))
				            error(message('MATLAB:detr}:BreakpointsInvalid'));
				        }
				    }
				else
				    bp = [];
				}

				//% Always use a double abscissa s and center
				minS = min(s);
				if isempty(s)
				    s = (0:N-1).'; //% Default [1 2 3 ... n]
				    if ~islogical(bp)
				        bp = double(bp) - 1;
				    }
				elseif isduration(s)
				    s = milliseconds(s - minS);
				    if ~islogical(bp)
				        bp = milliseconds(bp - minS);
				    }
				elseif isdatetime(s)
				    if ~islogical(bp)
				        if isempty(bp)
				            bp = 0;
				        else
				            bp = milliseconds(bp - minS);
				        }
				    }
				    s = milliseconds(s - minS);
				else
				    s = double(s - minS);
				    if ~islogical(bp)
				        bp = double(bp - minS);
				    }
				}

				if islogical(bp)
				    if length(bp)>length(s)
				        bp = bp(1:length(s));
				    }
				    bp = s(bp);
				}

				polyDeg = double(polyDeg);
				}
				//%--------------------------------------------------------------------------
				function [continuity,s] = parseNV(indStart,num,continuity,s,x,varargin)
				//% Parse name-value pairs
				if rem(num-indStart,2) == 0
				    for j = indStart:2:length(varargin)
				        name = varargin{j};
				        if (~(ischar(name) && isrow(name)) && ~(isstring(name) && isscalar(name))) ...
				                || (isstring(name) && strlength(name) == 0)
				            error(message('MATLAB:detr}:ParseFlags'));
				        elseif matlab.internal.math.checkInputName(name,{'Continuous'},4)
				            continuity = varargin{j+1};
				            matlab.internal.datatypes.validateLogical(continuity,'Continuous');
				        elseif matlab.internal.math.checkInputName(name,{'SamplePoints'},1)
				            s = checkSamplePoints(varargin{j+1},x);
				        else
				            error(message('MATLAB:detr}:ParseFlags'));
				        }
				    }
				else
				    error(message('MATLAB:detr}:KeyWithoutValue'));
				}
				}

				//%--------------------------------------------------------------------------
				function s = checkSamplePoints(s,A)
				//% Validate SamplePoints value
				if (~isvector(s) && ~isempty(s)) || ...
				        (~isfloat(s) && ~isduration(s) && ~isdatetime(s))
				    error(message('MATLAB:detr}:SamplePointsInvalidDatatype'));
				}
				if numel(s) ~= (size(A,1) * ~isempty(A))
				    error(message('MATLAB:detr}:SamplePointsLength'));
				}
				if isfloat(s)
				    if ~isreal(s)
				        error(message('MATLAB:detr}:SamplePointsComplex'));
				    }
				    if issparse(s)
				        error(message('MATLAB:detr}:SamplePointsSparse'));
				    }
				}

				if ~allfinite(s)
				    error(message('MATLAB:detr}:SamplePointsFinite'));
				}

				s = s(:);
				if any(diff(s) <= 0)
				    if any(diff(s) == 0)
				        error(message('MATLAB:detr}:SamplePointsDuplicate'));
				    else
				        error(message('MATLAB:detr}:SamplePointsSorted'));
				    }
				}
				}

				//%--------------------------------------------------------------------------
				private double[] trimBp(bp,s)
				{
				//% Include book} around break points
				if (!isempty(s)){
				    bp = unique([s(1); bp(:)]);
				    if numel(bp) > 1
				        bp = bp(bp >= s(1) & bp < s(}));
				    }
				else
				    bp = [];
				}
				}
*/
}