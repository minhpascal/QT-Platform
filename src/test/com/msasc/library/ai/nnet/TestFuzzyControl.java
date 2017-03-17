package test.com.msasc.library.ai.nnet;

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.ai.fuzzy.Control;
import com.qtplaf.library.ai.fuzzy.Segment;
import com.qtplaf.library.ai.fuzzy.function.Linear;
import com.qtplaf.library.trading.pattern.candle.CandlePattern.Size;

public class TestFuzzyControl {
	static final String VeryBig = "very_big";
	static final String Big = "big";
	static final String Medium = "mdium";
	static final String Small = "small";
	static final String VerySmall = "very_small";

	public static void main(String[] args) {
		Control control = getSizeControl();
		System.out.println(control.checkEQ(0.05, VerySmall));
		System.out.println(control.checkGE(0.11, Small));
		System.out.println(control.checkEQ(0.95, Big));
		System.out.println(control.checkGE(0.95, Big));
		System.out.println(control.checkEQ(0.95, VeryBig));
		System.out.println(control.getSegment(VerySmall).getFactor(0.01));	
		System.out.println(Control.class.getSimpleName());
	}
	
	private static Control getSizeControl() {
		List<Segment> segments = new ArrayList<>();
		segments.add(new Segment(Size.VerySmall, 0.10, 0.00, -1, new Linear()));
		segments.add(new Segment(Size.Small, 0.35, Math.nextUp(0.10), -1, new Linear()));
		segments.add(new Segment(Size.Medium, 0.65, Math.nextUp(0.35), 0, new Linear()));
		segments.add(new Segment(Size.Big, 0.85, Math.nextUp(0.65), 1, new Linear()));
		segments.add(new Segment(Size.VeryBig, 1.00, Math.nextUp(0.85), 1, new Linear()));
		Control control = new Control(segments);
		return control;
	}

}
