package simulation;
import java.util.ArrayList;

import boids.Fish;


import processing.core.PVector;

public abstract class Boid {

	public static enum Type { FISH, OBSTACLE, SHARK, PERSON, FOOD };
	
	// Used in taking cross-products, for constructing basis
	public static final PVector Z_VECTOR = new PVector( 0, 0, 1 );
	
	public final int ID;
	
	public static int colorCounter = 0;
	public static int boidCounter = 0;
	
	public PVector position;// In global
	public PVector speed;	// In global
	public PVector recentAccel; // Used in animating, in global
	public PVector[] basis; // Basis of local grid
	public int size;
	public boolean grouped; // Whether or not this boid has been grouped this anim cycle
	public int color;
	public int opacity;
	

	public Boid( PVector position, PVector speed, int size ) {
		
		ID = ++boidCounter;
		
		this.position = position;
		this.speed = speed;
		recentAccel = null;
		
		basis = new PVector[2];
		basis[0] = new PVector();
		basis[1] = new PVector();
		basis[0].set( speed );
		PVector.cross(Z_VECTOR, speed, basis[1]);
		basis[0].normalize();
		basis[1].normalize();
		
		this.size = size;
		grouped = false;		
	}
	
	public Boid( float xpos, float ypos, float xspeed, float yspeed, int size ) {
		this( new PVector( xpos, ypos ), new PVector( xspeed, yspeed), size );		
	}
	
	/*
	 * Makes all the changes of a timestep. Accel is the acceleration vector on the fish
	 * during this timestep 
	 */
	public abstract void step( ArrayList<Boid> flock );
	
	public abstract Boid.Type getTYPE();
	public abstract float getMAX_ACCEL();
	public abstract float getMIN_ACCEL();
	public abstract float getMAX_SPEED();
	public abstract float getMIN_SPEED();
	public abstract float getFEAR_WEIGHT();
	
	
	protected PVector avoidEgdes( PVector accel ) {
		
		if( position.x < Set.SCREEN_EdgeWidth ) {
			accel.x += redoRange( 1/(float)Math.pow( 1 + position.x, 3), 0, 1.5f*getMAX_ACCEL() ); 
		}
		else if( Set.SCREEN_Width - position.x < Set.SCREEN_EdgeWidth ) {
			accel.x -= redoRange( 1/(float)Math.pow( 1 + Set.SCREEN_Width-position.x, 3), 0, 1.5f*getMAX_ACCEL() ); 
		}
		
		if( position.y < Set.SCREEN_EdgeWidth ) {
			accel.y += redoRange( 1/(float)Math.pow( 1 + position.y, 1), 0, 1.5f*getMAX_ACCEL() ); 
		}
		else if( Set.SCREEN_Height - position.y < Set.SCREEN_EdgeWidth ) {
			accel.y -= redoRange( 1/(float)Math.pow( 1 + Set.SCREEN_Height-position.x, 3), 0, 1.5f*getMAX_ACCEL() ); 
		}
		
		return accel;
		
	}
	
	public static void group( ArrayList<Boid> school ) {
		
		ArrayList<ArrayList<Boid>> groups = new ArrayList<ArrayList<Boid>>();
		ArrayList<Integer> groupColors = new ArrayList<Integer>();
		ArrayList<Integer> colorArray = new ArrayList<Integer>();
		ArrayList<Integer> timesFound = new ArrayList<Integer>();
		
		int i,j,k;
		int max = 0;
		boolean found;
		
		for( Boid boid : school ) {
			boid.grouped = false;
		}
		
		/*for( i=0; i<boids.size(); i++ ) {
			if( boids.get(i).grouped == false ) {
				groups.add( new ArrayList<Boid>() );
				boids[i].groupHelper( boids, groups.get(groups.size()-1) );
			}
		}
		*/
		
		for( Boid boid : school ) {
			if( boid.grouped == false ) {
				groups.add( new ArrayList<Boid>() );
				boid.groupHelper( school, groups.get( groups.size()-1) );
			}
		}
		
		/*
		for( i=0; i<groups.size(); i++ ) {
			
			if( groups.get(i).size() > 1 ) {
			
				for( j=0; j<groups.get(i).size(); j++ ) {
					
					found = false;
				
					for( k=0; k<colorArray.size(); k++ ) {
						
						if( groups.get(i).get(j).color == colorArray.get(k) ) {
							timesFound.set(k, timesFound.get(k)+1 );
							found = true;
						}
					
					}
				
					if( found == false ) {
					
						colorArray.add( groups.get(i).get(j).color );
						timesFound.add( 1 );
				
					}
				}
			
				for( j=0; j<timesFound.size(); j++ ) {
					if( max < timesFound.get(j) ) {
						max = j;
					}
				}
				
				if( timesFound.get(max) >= groups.get(i).size()/2 ) {
					groupColors.add( colorArray.get(max) );
				} else {
					groupColors.add( colors[(colorCounter++)%colors.length] );
				}
			
			} else { // groups.get(i).size() == 1
				groupColors.add( groups.get(i).get(0).color );
			}
			
			
		}
		
		for( i=0; i<groups.size(); i++ ) {
			for( j=0; j<groups.size(); j++ ) {
				
				if( i==j ) {
					continue;
				}
				
				if( groupColors.get(i) == groupColors.get(j) ) {
					if( groups.get(i).size() > groups.get(j).size() ) {
						groupColors.remove(j);
						groupColors.add( j, colors[(colorCounter++)%colors.length] );
					} else {
						groupColors.remove(i);
						groupColors.add( i, colors[(colorCounter++)%colors.length] );
					}
				}
			}
		}
		
		for( i=0; i<groups.size(); i++ ) {
			for( j=0; j<groups.get(i).size(); j++ ) {
			
				groups.get(i).get(j).color = groupColors.get(i);
			
			}
		}
		*/
	}
	
	
	protected void groupHelper( ArrayList<Boid> school, ArrayList<Boid> group ) {
		
		group.add( this );
		grouped = true;
		
		for( Boid boid : school ) {
			/*if(    boids[i].grouped == false
				&& PVector.sub(boids[index].position, boids[i].position ).mag() < Fish.AWARE_RADIUS 
				&& PVector.sub(boids[index].speed,    boids[i].speed ).mag() < 5) {
			*/
			if(    boid.grouped == false
				//&& PVector.sub(boids[index].speed,    boids[i].speed ).mag() < 5
				&& (   PVector.sub(position, boid.position ).mag() < 1.5*50
					|| Math.abs(position.x-boid.position.x-Set.SCREEN_Width) < 3*50
					|| Math.abs(position.y-boid.position.y-Set.SCREEN_Height) < 3*50 )  ) {
				
				boid.groupHelper( school, group);
				
			}
		}
		
	}
	/*
	 * Convenience function that returns the angle from the x-axis to the velocity vector.
	 * Return values from -PI to +PI
	 */
	public double getHeading() {
		return Math.atan2(speed.y, speed.x);
	}
		
	/*
	 * Creates a vector that is the orthogonal projection of "of" onto "onto"
	 */
	public static PVector orthProj( PVector of, PVector onto) {
		return PVector.mult(onto, PVector.dot(of, onto)/onto.mag() );
	}
	
	/*
	 * Inverts a 2x2 matrix. matrix is a an array of PVectors of length 2, so that:
	 * 		matrix[0] = { a, b }  and matrix[1] = { c, d }
	 * and we are inverting the matrix
	 *  	{ { a, c },
	 *  	  { b, d } }
	 * We return:
	 * 				{ {  d, -c } ,
	 *  1/(ad-bc) *   { -b,  a } }
	 *  
	 *  NOTE: ad-bc != 0 !!  (meaning matrix[0] and matrix[1] must be linearly indep!)
	 */
	public static PVector[] inverse( PVector[] matrix ) {
		
		float a, b, c, d, det;
		
		a = matrix[0].x;
		b = matrix[0].y;
		c = matrix[1].x;
		d = matrix[1].y;
		
		det = a*d - c*b; // the determinant
		
		if( a*d-c*b == 0 ) {  // If determinant is 0, inverse doesn't exist. Return null
			return null;
		}
		
		PVector[] inverse = new PVector[2];
		
		inverse[0] = new PVector( d/det, -c/det );
		inverse[1] = new PVector( -b/det, a/det );
		
		return inverse;
		
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		return ( obj instanceof Boid && ID == ((Boid) obj).ID );
	}

	public static PVector matrixMult( PVector[] matrix, PVector vector ) {
		
		return new PVector( matrix[0].x*vector.x + matrix[0].y*vector.y,
							matrix[1].x*vector.x + matrix[1].y*vector.y  );
	
	}
	
	public static PVector[] matrixMultParallel( PVector[] matrix, PVector[] arrayOfVectors ) {
		
		PVector[] answer = new PVector[ arrayOfVectors.length ];
		
		int length = arrayOfVectors.length;
		
		for( int i=0; i<length; i++ ) {
			answer[i] = matrixMult( matrix, arrayOfVectors[i] );
		}
		
		return answer;
	}
	
	public static float redoRange( float value, float targetMin, float targetMax,
								   				float sourceMin, float sourceMax ) {
		return (value-sourceMin) * ((targetMax-targetMin)/(sourceMax-sourceMin)) + targetMax;
	}
	
	public static float redoRange( float value, float targetMin, float targetMax ) {
		return redoRange( value, targetMin, targetMax, 0, 1 );
	}

}
