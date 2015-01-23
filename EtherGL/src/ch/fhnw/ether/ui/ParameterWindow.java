package ch.fhnw.ether.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.RenderProgram;

public class ParameterWindow {
	static final float S         = 1000f;
	static final int   NUM_TICKS = 5;

	private static NumberFormat FMT = new DecimalFormat("#.##");

	static class ParamUI implements ChangeListener, ActionListener {
		JLabel                     label;
		JSlider                    slider;
		JComboBox<String>          combo;
		AbstractRenderCommand<?,?> cmd;
		Parameter                  p;
		float                      def;

		ParamUI(AbstractRenderCommand<?,?> cmd, Parameter param) {
			Hashtable<Integer, JLabel> labels = new Hashtable<>();
			float d = param.getMax() - param.getMin();
			for(int i = 0; i < NUM_TICKS; i++) {
				float val = param.getMin() + ((i * d) / (NUM_TICKS-1));
				labels.put(Integer.valueOf((int)(val * S)), new JLabel(FMT.format(val))); 
			}
			this.cmd    = cmd;
			this.p      = param;
			this.def    = cmd.getVal(param);
			this.label  = new JLabel(param.getDescription());
			switch(p.getType()) {
			case RANGE:
				this.slider = new JSlider((int)(param.getMin() * S), (int)(param.getMax() * S), (int)(cmd.getVal(p) * S));
				this.slider.setPaintLabels(true);
				this.slider.setPaintTicks(true);
				this.slider.setLabelTable(labels);
				this.slider.addChangeListener(this);
				break;
			case ITEMS:
				this.combo = new JComboBox<>(param.getItems());
				this.combo.addActionListener(this);
				this.combo.setSelectedIndex((int)(cmd.getVal(p)));
				break;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			cmd.setVal(p, slider.getValue() / S);
		}

		public void reset() {
			if(slider != null)
				slider.setValue((int)(def * S));
			if(combo != null)
				combo.setSelectedIndex((int) def);
		}

		public void zero() {
			if(slider != null)
				slider.setValue(0);
			if(combo != null)
				combo.setSelectedIndex(0);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cmd.setVal(p, combo.getSelectedIndex());
		}
	}

	public ParameterWindow(final AbstractRenderCommand<?,?> src) {
		this(null, src);
	}

	public ParameterWindow(final JComponent addOn, final AbstractRenderCommand<?,?> src) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame  = new JFrame("Parameters");
				frame.setLayout(new BorderLayout());
				if(addOn != null)
					frame.add(addOn, BorderLayout.NORTH);
				frame.add(createUIRecr(src), BorderLayout.CENTER);
				frame.pack();
				frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getSize().width, frame.getLocation().y);
				frame.setVisible(true);
			}

			JComponent createUI(AbstractRenderCommand<?,?> cmd) {
				JPanel        result = new JPanel();	
				result.setBorder(new TitledBorder(cmd.getClass().getName()));
				Parameter[] params = cmd.getParameters();
				result.setLayout(new GridLayout(params.length + 1, 2, 0, 0));
				if(params.length > 0) {
					final ParamUI[] uis = new ParamUI[params.length];
					for(int i = 0; i < uis.length; i++) {
						uis[i] = new ParamUI(cmd, params[i]);
						result.add(uis[i].label);
						if(uis[i].slider != null)
							result.add(uis[i].slider);
						if(uis[i].combo != null)
							result.add(uis[i].combo);
					}
					JButton reset = new JButton("Reset");
					reset.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							for(ParamUI p : uis) p.reset();	
						}
					});
					result.add(reset);

					JButton zero = new JButton("Zero");
					zero.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							for(ParamUI p : uis) p.zero();	
						}
					});
					result.add(zero);
				} else {
					JTextArea text = new JTextArea(cmd.toString(), 2, 30);
					text.setWrapStyleWord(false);
					text.setEditable(false);
					result.add(text);
				}
				return result;
			}


			JPanel createUIRecr(AbstractRenderCommand<?,?> src) {
				Insets insets = new Insets(0, 0, 0, 0);
				JPanel result = new JPanel();
				result.setLayout(new GridBagLayout());
				JComponent cmp = createUI(src);
				int w = 1;
				if(src instanceof RenderProgram<?>) {
					RenderProgram<?> program = (RenderProgram<?>)src;
					program.addListener((RenderProgram<?> prog,
							AbstractRenderCommand<?, ?>[] oldProgram,
							AbstractRenderCommand<?, ?>[] newProgram)->{
								result.removeAll();
								int y = 0;
								for(AbstractRenderCommand<?, ?> cmd : newProgram)
									result.add(createUIRecr(cmd), new GridBagConstraints(1, y++, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
								Component c = SwingUtilities.getRoot(result);
								if(c instanceof JFrame)
									((JFrame)c).pack();
								else
									c.validate();;
							});
					int y = 0;
					for(AbstractRenderCommand<?, ?> cmd : program.getProgram())
						result.add(createUIRecr(cmd), new GridBagConstraints(1, y++, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
				} else {
					result.add(cmp, new GridBagConstraints(0, 0, w, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
				}
				return result;
			}
		});

	}
}