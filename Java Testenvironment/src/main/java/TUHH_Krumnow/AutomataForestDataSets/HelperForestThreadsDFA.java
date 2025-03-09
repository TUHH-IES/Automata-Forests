package TUHH_Krumnow.AutomataForestDataSets;

import net.automatalib.automata.fsa.DFA;
import net.automatalib.words.Alphabet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class HelperForestThreadsDFA implements Callable {

    private Alphabet<Character> sigma;
    List<String> pos;
    List<String> neg;

    public HelperForestThreadsDFA(Alphabet<Character> _sigma,List<String> _pos,List<String> _neg){
        pos = new ArrayList<>(_pos);
        neg = new ArrayList<>(_neg);
        sigma = _sigma;
    }

    @Override
    public Object call() throws Exception {
        return HelperFunctions.computeModelBlueFringe(sigma,HelperFunctions.transformFromListToCollection(pos),HelperFunctions.transformFromListToCollection(neg));
    }


}
