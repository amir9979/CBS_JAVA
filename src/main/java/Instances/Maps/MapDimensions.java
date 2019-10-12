package Instances.Maps;



public class MapDimensions{


    public int numOfDimensions;
    public int xAxis_length;
    public int yAxis_length;
    public int zAxis_length;


    public MapDimensions(){
        this.numOfDimensions = 0; // Indicates unknown size
    }

    public MapDimensions(int xAxis_length, int yAxis_length) {
        this.numOfDimensions = 2;
        this.xAxis_length = xAxis_length;
        this.yAxis_length = yAxis_length;
    }


    public MapDimensions(int xAxis_length, int yAxis_length, int zAxis_length) {
        this.numOfDimensions = 3;
        this.xAxis_length = xAxis_length;
        this.yAxis_length = yAxis_length;
        this.zAxis_length = zAxis_length;
    }


    public MapDimensions(int[] dimensions){
        // Blocking - check format, if {16,16} is {x,y}

        switch ( dimensions.length ){
            case 2:
                this.numOfDimensions = 2;
                this.xAxis_length = dimensions[0];
                this.yAxis_length = dimensions[1];
                break;

            case 3:
                this.numOfDimensions = 3;
                this.xAxis_length = dimensions[0];
                this.yAxis_length = dimensions[1];
                this.zAxis_length = dimensions[2];
                break;
        }


    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MapDimensions)) return false;
        MapDimensions that = (MapDimensions) other;
        return  this.numOfDimensions    == that.numOfDimensions &&
                this.xAxis_length       == that.xAxis_length &&
                this.yAxis_length       == that.yAxis_length &&
                this.zAxis_length       == that.zAxis_length;
    }


}
