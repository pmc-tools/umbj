import io.github.pmctools.umbj.UMBException;
import io.github.pmctools.umbj.UMBIndex;
import io.github.pmctools.umbj.UMBReader;
import io.github.pmctools.umbj.UMBType;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple demo of using UMBReader to read an MDP from a UMB file.
 */
public class Demo
{
    public static void main(String[] args)
    {
        try {
            // Load an example UMB file
            // (wget https://github.com/pmc-tools/umb/raw/refs/heads/main/example/mdp.umb)
            UMBReader umbReader = new UMBReader(new File("mdp.umb"));
            UMBIndex umbIndex = umbReader.getUMBIndex();

            // Check model type
            UMBIndex.ModelType modelType = umbIndex.getModelType();
            UMBType branchType = umbIndex.getBranchProbabilityType();
            if (modelType != UMBIndex.ModelType.MDP || branchType.type != UMBType.Type.DOUBLE) {
                System.out.println("Unexpected model type: " + modelType + "(" + branchType + ")");
                System.exit(1);
            }

            // Extract model stats
            int numStates = (int) umbIndex.getNumStates();
            int numChoices = (int) umbIndex.getNumChoices();
            int numBranches = (int) umbIndex.getNumBranches();
            System.out.println("States: " + numStates + ", choices: " + numChoices + ", transitions: " + numBranches);

            // Extract action strings
            ArrayList<String> actionStrings = null;
            if (umbReader.hasChoiceActionStrings()) {
                actionStrings = new ArrayList<>();
                umbReader.extractChoiceActionStrings(actionStrings::add);
                System.out.println("Actions: " + actionStrings);
            }

            // Extract transition function data
            IntList stateChoiceOffsets = new IntArrayList(numStates + 1);
            umbReader.extractStateChoiceOffsets(l -> stateChoiceOffsets.add((int) l));
            IntList choiceBranchOffsets = new IntArrayList(numChoices + 1);
            umbReader.extractChoiceBranchOffsets(l -> choiceBranchOffsets.add((int) l));
            IntList branchSuccessors = new IntArrayList(numBranches);
            umbReader.extractBranchTargets(l -> branchSuccessors.add((int) l));
            DoubleList branchProbabilities = new DoubleArrayList(numBranches);
            umbReader.extractBranchProbabilities(d -> branchProbabilities.add((double) d));

            // Extract action info
            IntList choiceActionIndices = null;
            if (umbReader.hasChoiceActionIndices()) {
                choiceActionIndices = new IntArrayList(numChoices);
                umbReader.extractChoiceActionIndices(choiceActionIndices::add);
            }
            boolean hasActions = choiceActionIndices != null && actionStrings != null;

            // Extract transition function from CSR
            int iLo = 0, iHi = 0;
            int jLo = 0, jHi = 0;
            for (int s = 0; s < numStates; s++) {
                iLo = iHi;
                iHi = stateChoiceOffsets.getInt(s + 1);
                int iCount = 0;
                for (int i = iLo; i < iHi; i++) {
                    String action = hasActions ? actionStrings.get(choiceActionIndices.getInt(i)) : "?";
                    System.out.print(s + " -" + action + "->");
                    jLo = jHi;
                    jHi = choiceBranchOffsets.getInt(i + 1);
                    for (int j = jLo; j < jHi; j++) {
                        System.out.print(" " + branchProbabilities.getDouble(j) + ":" + branchSuccessors.getInt(j));
                    }
                    iCount++;
                    System.out.println();
                }
            }

        } catch (UMBException e) {
            System.out.println("UMB error: " + e.getMessage());
            System.exit(1);
        }
    }
}