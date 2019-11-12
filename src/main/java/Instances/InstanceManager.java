package Instances;

import java.util.Objects;
import java.util.Stack;

public class InstanceManager {

    private String sourceDirectory;
    private I_InstanceBuilder instanceBuilder;
    private InstanceProperties instanceProperties;

    private Stack<InstancePath> instancesPaths_stack = new Stack<InstancePath>();

    public I_InstanceBuilder getInstanceBuilder() {
        return instanceBuilder;
    }

    public Stack<InstancePath> getInstancesPaths_stack() {
        return instancesPaths_stack;
    }

    public InstanceManager(String sourceDirectory,
                           I_InstanceBuilder instanceBuilder,
                           InstanceProperties properties) {

        this(sourceDirectory, instanceBuilder);

        // Set properties
        this.instanceProperties = properties;

        if( this.instanceProperties == null){
            this.instanceProperties = new InstanceProperties();
        }
        this.instanceProperties.mapSize.setMapOrientation(instanceBuilder.getMapOrientation());



    }

    public InstanceManager(I_InstanceBuilder instanceBuilder){
        this.instanceBuilder = instanceBuilder;
        this.instanceProperties = new InstanceProperties(this.instanceBuilder.getMapOrientation());
    }


    public InstanceManager(String sourceDirectory,
                           I_InstanceBuilder instanceBuilder){

        this.sourceDirectory = sourceDirectory;
        this.instanceBuilder = instanceBuilder;

        if(this.sourceDirectory != null){
            this.addInstancesPaths_toStack( this.sourceDirectory );
        }
    }


    public MAPF_Instance getSpecificInstance(InstancePath currentPath){

        String regexSeparator = "\\\\"; //  this is actually: '\\'
        String[] splitedPath = currentPath.path.split(regexSeparator); // Done - check if the "/" is correct
        String instanceName = splitedPath[splitedPath.length-1];

        this.instanceBuilder.prepareInstances(instanceName, currentPath, this.instanceProperties);
        return this.instanceBuilder.getNextExistingInstance();

    }

    public MAPF_Instance getNextInstance(){
        /* Returns null in case of an error */

        // Tries to get the next Existing Instance
        MAPF_Instance nextInstance = this.instanceBuilder.getNextExistingInstance();
        while(nextInstance == null){

            if(this.instancesPaths_stack.empty()){
                // NiceToHave - create new instances
                return null;
            }

            InstancePath currentPath = this.instancesPaths_stack.pop();


            nextInstance = getSpecificInstance(currentPath);

        }

        return nextInstance;
    }


    private void addInstancesPaths_toStack(String directoryPath){

        InstancePath[] instancePaths = this.instanceBuilder.getInstancesPaths(directoryPath);

        for (int i = 0; i < instancePaths.length ; i++) {
            this.instancesPaths_stack.push(instancePaths[i]);
        }

    }

    /***  =Instance path wrapper=  ***/

    public static class InstancePath{

        public final String path;
        public InstancePath(String path){ this.path = path; }
    }

    public static class Moving_AI_Path extends InstancePath{

        public final String scenarioPath;
        public Moving_AI_Path(String mapPath, String scenarioPath) {
            super(mapPath);
            this.scenarioPath = scenarioPath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Moving_AI_Path)) return false;
            Moving_AI_Path that = (Moving_AI_Path) o;
            return Objects.equals(scenarioPath, that.scenarioPath) &&
                    Objects.equals(this.path, that.path);
        }

    }

}
