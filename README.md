# RANSAC and Hough Transform

Java implementation of the Hough transform and RANSAC algorithm

## RANSAC

We start by choosing 3 random points from the data set and construct a circle that passes through them. Then we add all other points that are within a given distance from the circle's circumference. We repeat this procedure maxIter times, while keeping track of the highest number of points for a circle so far. When the iterations have finished, the circle with highest number of points is chosen as the best model.

### Methods

- (+) showCanvas() *Displays the data points and the calculated circles on a canvas.*
- (+) execute() *Runs the RANSAC algorithm.*
- (-) getOffset() *Calculate the distance between a point and the center of a circle*
- (-) getCircle() *Find the circle that passes through the three given points*
- (-) getNPoints() *Choose n data points at random* 

### Illustrations

Points from *points.r.data* are being used.

- Cirlces (x, y, r, number of points)
  - 20, 100, 100, 100
- Noice: 900 points randomly selected between -200 and 200
- smallestRadius=92.17273473900397
- highestRadius=111.01668853035679
- 18.843953791352817

![PS1](http://i.imgur.com/fs2WgVy.png)

![PS2](http://i.imgur.com/U5HaP7R.png)

![PS3](http://i.imgur.com/RR8M50p.png)

## Hough Transform

A 3D array - the accumulator - is used to store information about potential circles. The parameters are the x and y coordiantes of the center point and the radius For every point in the data set, the value of every cell that could represent the circle that passes through the point is incremented. Cells that exceed a certain threshold are chosen as candidate circles. ??????????????????????????????????????????

### Methods

- (+) execute() *Executes the Hough Transform algorithm.*
- (+) showCanvas() *Render view based on this.pixels.*
- (-) getCircles() *Finds every other circle that has @point.getY() and @point.getX() as its center*

### Illustrations

Points from *points.ht.data* are being used.

- Cirlces (x, y, r, number of points)
  - 0, 0, 50, 50
  - 100, 140, 110, 50
  - 0, 50, 50, 50
  - 20, 100, 100, 100
- Noice: 120 points randomly selected between -200 and 200

![PS1](http://i.imgur.com/rYqX67O.png)