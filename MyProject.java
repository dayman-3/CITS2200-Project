import java.util.HashSet;
import java.util.Stack;

public class MyProject implements Project {

    public MyProject() {}
    
    /**
     * Compute the number of pixels that change when performing a black flood-fill from the pixel at (row, col) in the given image.
     * A flood-fill operation changes the selected pixel and all contiguous pixels of the same colour to the specified colour.
     * A pixel is considered part of a contiguous region of the same colour if it is exactly one pixel up/down/left/right of another pixel in the region.
     * 
     * @param image The greyscale image as defined above
     * @param row The row index of the pixel to flood-fill from
     * @param col The column index of the pixel to flood-fill from
     * @return The number of pixels that changed colour when performing this operation
     */
    public int floodFillCount(int[][] image, int row, int col) {
        // index [y][x] -> y * cols + x
        // x -> position % cols, y -> position / cols

        if (image[row][col] == 0) return 0;
        // Edge case that the algorithm won't figure out on its own

        int rows = image.length;
        int cols = image[0].length;
        int fill = image[row][col];
        
        Stack<Integer> stacky_boi = new Stack<>();
        HashSet<Integer> visited = new HashSet<>(); // hashset allows checking in constant time

        stacky_boi.push(row*cols+col);
        visited.add(row*cols+col);
        int xpos = col;
        int ypos = row;
        while (!stacky_boi.isEmpty()) {
            int curr = stacky_boi.peek();
            xpos = curr % cols;
            ypos = curr / cols;

            // Exactly one of these if statements will run each iteration,
            // or none will and the code at the end gets executed.
            // Here we look left, down, right, and up and move in the
            // first of these directions that is legal to fill.
            if (xpos > 0 && !visited.contains(curr-1) && image[ypos][xpos-1] == fill) {
                curr -= 1;
                stacky_boi.push(curr);
                visited.add(curr);
                
                continue;
            }
            if (ypos > 0 && !visited.contains(curr-cols) && image[ypos-1][xpos] == fill) {
                curr -= cols;
                stacky_boi.push(curr);
                visited.add(curr);

                continue;
            }
            if (xpos < cols-1 && !visited.contains(curr+1) && image[ypos][xpos+1] == fill) {
                curr += 1;
                stacky_boi.push(curr);
                visited.add(curr);

                continue;
            }
            if (ypos < rows-1 && !visited.contains(curr+cols) && image[ypos+1][xpos] == fill) {
                curr += cols;
                stacky_boi.push(curr);
                visited.add(curr);

                continue;
            }
            // if we get here then none of the sides were checked
            // meaning we have to backtrack to get directions that weren't 
            // checked before.
            stacky_boi.pop();
        }

        return visited.size();
    }

    /**
     * Compute the total brightness of the brightest exactly k*k square that appears in the given image.
     * The total brightness of a square is defined as the sum of its pixel values.
     * You may assume that k is positive, no greater than R or C, and no greater than 2048.
     * 
     * @param image The greyscale image as defined above
     * @param k the dimension of the squares to consider
     * @return The total brightness of the brightest square
     */
    public int brightestSquare(int[][] image, int k) {
        int rows = image.length;
        int cols = image[0].length;

        int square1 = 0;
        for (int y = 0; y < k; y++) {
            for (int x = 0; x < k; x++) {
                square1 += image[y][x];
            }
        } // O(k)

        int [][] hTotals = new int [rows][cols];
        int [][] vTotals = new int [rows][cols];
        int [][] totals = new int [rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (x == 0 && y == 0) {
                    totals[y][x] = image[y][x];
                    continue;
                }
                if (y == 0) {
                    hTotals[y][x] = hTotals[y][x-1] + image[y][x]; 
                    vTotals[y][x] = image[y][x];
                    continue;
                }
                if (x == 0) {
                    hTotals[y][x] = image[y][x];
                    vTotals[y][x] = vTotals[y-1][x] + image[y][x];
                    continue;
                }
                vTotals[y][x] = vTotals[y-1][x] + image[y][x];
                hTotals[y][x] = hTotals[y][x-1] + image[y][x];       
                // for some cases, the vertical or horizontal strip sum is what's
                // on the image at that point, otherwise it can be calculated with
                // the previous value so we have to know if we're on an edge or not.
            }
        } // O(P)

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (y == 0) {
                    totals[y][x] = hTotals[y][x];
                    continue;
                }
                if (x == 0) {
                    totals[y][x] = vTotals[y][x];
                    continue;
                }
                totals[y][x] = totals[y-1][x-1] + 
                               hTotals[y][x-1] + 
                               vTotals[y-1][x] + 
                               image[y][x];
                // The cumulative brightness can be derived from other values in
                // the same array as is shown in the report
            }
        } // O(P)

        int brightest = 0;
        for (int y = 0; y <= rows-k; y++) {
            for (int x = 0; x <= cols-k; x++) {
                if (y == 0 && x == 0) {
                    brightest = square1;
                    continue;
                }
                int upTotal = y != 0 ? totals[y-1][x+k-1] : 0;
                int leftTotal = x != 0 ? totals[y+k-1][x-1] : 0;
                int cornerTotal = y != 0 && x != 0 ? totals[y-1][x-1] : 0;
                int sum = totals[y+k-1][x+k-1] - upTotal - leftTotal + cornerTotal;
                if (sum > brightest) brightest = sum;
                // as explained in the report, we have to calculate the sums of certain sections
                // in order to get the square brightness. However in some cases we don't account
                // for these, such as calculating a square in the top right corner or one of
                // those edges, meaning we set them to zero when necessary.
            }
        } // O(P)

        return brightest;
    }

    /**
     * Compute the maximum brightness that MUST be encountered when drawing a path from the pixel at (ur, uc) to the pixel at (vr, vc).
     * The path must start at (ur, uc) and end at (vr, vc), and may only move one pixel up/down/left/right at a time in between.
     * The brightness of a path is considered to be the value of the brightest pixel that the path ever touches.
     * This includes the start and end pixels of the path.
     * 
     * @param image The greyscale image as defined above
     * @param ur The row index of the start pixel for the path
     * @param uc The column index of the start pixel for the path
     * @param vr The row index of the end pixel for the path
     * @param vc The column index of the end pixel for the path
     * @return The minimum brightness of any path from (ur, uc) to (vr, vc)
     */
    public int darkestPath(int[][] image, int ur, int uc, int vr, int vc) {
        // index [y][x] -> y * cols + x
        // x -> z % cols, y -> z / cols
        if (ur == vr && uc == vc) return image[ur][uc];
        // Simple edge case handler that would save a lot of time

        int rows = image.length;
        int cols = image[0].length;
        
        boolean [] colours = new boolean [256];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                colours[image[y][x]] = true;
            }
        }
        // This array is helpful to make sure we don't run searches 
        // using a colour that does not appear in the array, as we will already
        // know that the return value can not be said colour.

        int darkest = 255;

        for (int c = 255; c >= 0; c--) {
            if (colours[c]) {
                Stack<Integer> stacky_boi = new Stack<>();
                HashSet<Integer> visited = new HashSet<>();
                // The stack and hashset are used the same way as in floodFillCount(),
                // as we are effectively running a flood fill for every possible colour
                // (only the ones that are in the image)
    
                stacky_boi.push(ur*cols+uc);
                visited.add(ur*cols+uc);
    
                int curr = ur*cols+uc;
                boolean stop = false;
                while (!stacky_boi.isEmpty() && !stop) {
                    curr = stacky_boi.peek();
                    int xpos = curr % cols;
                    int ypos = curr / cols;
    
                    if (xpos > 0 && !visited.contains(curr-1) && image[ypos][xpos-1] < c) {
                        curr -= 1;
                        stacky_boi.push(curr);
                        visited.add(curr);
    
                        // See last if statement for explanation
                        if (curr == vr*cols+vc && image[ur][uc] != c) {
                            stop = true;
                            darkest = c-1;
                        }
                        continue;
                    }
    
                    if (ypos > 0 && !visited.contains(curr-cols) && image[ypos-1][xpos] < c) {
                        curr -= cols;
                        stacky_boi.push(curr);
                        visited.add(curr);
    
                        if (curr == vr*cols+vc && image[ur][uc] != c) {
                            stop = true;
                            darkest = c-1;
                        }

                        continue;
                    }
    
                    if (xpos < cols-1 && !visited.contains(curr+1) && image[ypos][xpos+1] < c) {
                        curr += 1;
                        stacky_boi.push(curr);
                        visited.add(curr);
        
                        if (curr == vr*cols+vc && image[ur][uc] != c) {
                            stop = true;
                            darkest = c-1;
                        }

                        continue;
                    }
                    if (ypos < rows-1 && !visited.contains(curr+cols) && image[ypos+1][xpos] < c) {
                        curr += cols;
                        stacky_boi.push(curr);
                        visited.add(curr);
    
                        // A path was found without passing through this colour - so
                        // we assume the next next colour to be darkest. Also make sure to check
                        // the start pixel
                        if (curr == vr*cols+vc && image[ur][uc] != c) {
                            stop = true;
                            darkest = c-1;
                        }

                        continue;
                    }
                    // Nothing is found at this step, so backtrack.
                    stacky_boi.pop();
                    
                }
                // The search has ended - if we're at the destination continue,
                // if not then this colour is the darkest it will get.
                if (curr != vr*cols+vc) return darkest;
            } else {
                // this colour is not in the image, so we don't bother
                // running the search bounded by it
                darkest--;
            }
        }
        // This code is never actually reached, provided all pixels are in
        // the correct range for brightness.
        return darkest;
    }

    /**
     * Compute the results of a list of queries on the given image.
     * Each query will be a three-element int array {r, l, u} defining a row segment. You may assume l < u.
     * A row segment is a set of pixels (r, c) such that r is as defined, l <= c, and c < u.
     * For each query, find the value of the brightest pixel in the specified row segment.
     * Return the query results in the same order as the queries are given.
     * 
     * @param image The greyscale image as defined above
     * @param queries The list of query row segments
     * @return The list of brightest pixels for each query row segment
     */
    public int[] brightestPixelsInRowSegments(int[][] image, int[][] queries) {
        int nQueries = queries.length;
        int [] result = new int [nQueries];

        // For every query, update the result array with the max
        // brightness in each segment specified
        for (int q = 0; q < nQueries; q++) {
            int max = 0;
            int row = queries[q][0];
            for (int cell = queries[q][1]; cell < queries[q][2]; cell++) {
                if (image[row][cell] > max) max = image[row][cell]; 
            }
            result[q] = max;
        }
        
        return result;
    }
}
