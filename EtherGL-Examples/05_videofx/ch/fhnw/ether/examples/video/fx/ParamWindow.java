package ch.fhnw.ether.examples.video.fx;

import java.awt.Dimension;
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

import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.IFX;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.ether.video.fx.IVideoFX;

public class ParamWindow {
	static final float S         = 1000f;
	static final int   NUM_TICKS = 5;

	private static NumberFormat FMT = new DecimalFormat("#.##");

	static class ParamUI implements ChangeListener, ActionListener {
		JLabel            label;
		JSlider           slider;
		JComboBox<String> combo;
		IFX               fx;
		FXParameter       p;
		float             def;

		ParamUI(IFX fx, FXParameter param) {
			Hashtable<Integer, JLabel> labels = new Hashtable<>();
			float d = param.getMax() - param.getMin();
			for(int i = 0; i < NUM_TICKS; i++) {
				float val = param.getMin() + ((i * d) / (NUM_TICKS-1));
				labels.put(Integer.valueOf((int)(val * S)), new JLabel(FMT.format(val))); 
			}
			this.fx     = fx;
			this.p      = param;
			this.def    = fx.getVal(param);
			this.label  = new JLabel(param.getDescription());
			switch(p.getType()) {
			case RANGE:
				this.slider = new JSlider((int)(param.getMin() * S), (int)(param.getMax() * S), (int)(fx.getVal(p) * S));
				this.slider.setPaintLabels(true);
				this.slider.setPaintTicks(true);
				this.slider.setLabelTable(labels);
				this.slider.addChangeListener(this);
				break;
			case ITEMS:
				this.combo = new JComboBox<>(param.getItems());
				this.combo.addActionListener(this);
				break;
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			fx.setVal(p, slider.getValue() / S);
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
			fx.setVal(p, combo.getSelectedIndex());
		}
	}

	public ParamWindow(final IVideoFrameSource src) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame  = new JFrame("Parameters");
				frame.setLayout(new GridLayout(1, 1));
				frame.add(createUIRecr(src));
				frame.pack();
				frame.setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - frame.getSize().width, frame.getLocation().y);
				frame.setVisible(true);
			}

			JComponent createUI(IVideoFrameSource src) {
				if(src instanceof IVideoFX) {
					IVideoFX      fx     = (IVideoFX)src;
					JPanel        result = new JPanel();	
					result.setBorder(new TitledBorder(src.getClass().getName()));
					FXParameter[] params = fx.getParameters();
					result.setLayout(new GridLayout(params.length + 1, 2, 0, 0));
					final ParamUI[] uis = new ParamUI[params.length];
					for(int i = 0; i < uis.length; i++) {
						uis[i] = new ParamUI(fx, params[i]);
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

					return result;
				}
				JTextArea result = new JTextArea(src.toString(), 5, 30);
				result.setWrapStyleWord(false);
				result.setEditable(false);
				result.setPreferredSize(new Dimension(256, 128));
				return result;
			}


			JPanel createUIRecr(IVideoFrameSource src) {
				Insets insets = new Insets(0, 0, 0, 0);
				JPanel result = new JPanel();
				result.setLayout(new GridBagLayout());
				JComponent cmp = createUI(src);
				int w = 1;
				if(src instanceof AbstractVideoFX) {
					AbstractVideoFX fx = (AbstractVideoFX)src;
					w = fx.getNumSources();
				}
				result.add(cmp, new GridBagConstraints(0, 0, w, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
				if(src instanceof AbstractVideoFX) {
					AbstractVideoFX fx = (AbstractVideoFX)src;
					int x = 0;
					for(IVideoFrameSource source : fx.getSources())
						result.add(createUIRecr(source), new GridBagConstraints(x++, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

				}
				return result;
			}
		});

	}
}