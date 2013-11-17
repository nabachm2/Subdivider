Subdivider
==========

Implementation of catmull-clark subdivsion for quads

This program provides an implementation of Catmull-Clark's quad subdivision algorithm which 
is used to transform a continues quadmesh into a "smoother mesh". 

USING THE PROGRAM:

This program uses JOGL, a java wrapper for openGL, to display the final mesh. As such this project
must be compiled with the dependency jars within the "lib" folder. 

For simplicity I have included an executable jar file that allows the program to be run from the command 
line.

The usage is as follows:
java -jar Subdivider.jar infile [-subdivisions divisions] [-outfile outfile] [-show] 

where:
infile: is the file that contains the initial data
-subdivisions: the number of times to subdivide the original mesh, where a value of 0 is no subdivisions
-outfile: the file to write the newly subdivided mesh. Note: this file must not already exist.
-show: if specified, the final mesh will show in a window, where the user can view the new mesh.


WHAT THIS PROGRAM IS CAPABLE OF:
This program requires an input mesh's vertices to be in the form where each
line in the file holds the vertices of a quad where every value is separated by spaces

Further the program will write out the data in the same way, with the inclusion of generated normals

For simplicity I have included 2 test files Cube.txt and Test.txt who define the mesh
for a Cube and a T shape respectively.
