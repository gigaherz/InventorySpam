package gigaherz.inventoryspam.config;

import net.minecraftforge.fml.client.config.*;

/*
public class ExponentialNumberSliderEntry extends GuiConfigEntries.ButtonEntry
{
    protected final double beforeValue;

    public ExponentialNumberSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
    {
        super(owningScreen, owningEntryList, configElement, new GuiExponentialSlider(0, owningEntryList.controlX, 0, owningEntryList.controlWidth, 18,
                "", "%", Double.valueOf(configElement.getMinValue().toString()), Double.valueOf(configElement.getMaxValue().toString()),
                Double.valueOf(configElement.get().toString()), configElement.getType() == ConfigGuiType.DOUBLE, true));

        this.beforeValue = Double.valueOf(configElement.get().toString());
    }

    @Override
    public void updateValueButtonText()
    {
        ((GuiSlider) this.btnValue).updateSlider();
    }

    @Override
    public void valueButtonPressed(int slotIndex)
    {
    }

    @Override
    public boolean isDefault()
    {
        return ((GuiSlider) this.btnValue).getValue() == Double.valueOf(configElement.getDefault().toString());
    }

    @Override
    public void setToDefault()
    {
        if (this.enabled())
        {
            ((GuiSlider) this.btnValue).setValue(Double.valueOf(configElement.getDefault().toString()));
            ((GuiSlider) this.btnValue).updateSlider();
        }
    }

    @Override
    public boolean isChanged()
    {
        return ((GuiSlider) this.btnValue).getValue() != beforeValue;
    }

    @Override
    public void undoChanges()
    {
        if (this.enabled())
        {
            ((GuiSlider) this.btnValue).setValue(beforeValue);
            ((GuiSlider) this.btnValue).updateSlider();
        }
    }

    @Override
    public boolean saveConfigElement()
    {
        if (this.enabled() && this.isChanged())
        {
            configElement.set(((GuiSlider) this.btnValue).getValue());
            return configElement.requiresMcRestart();
        }
        return false;
    }

    @Override
    public Object getCurrentValue()
    {
        return ((GuiSlider) this.btnValue).getValue();
    }

    @Override
    public Object[] getCurrentValues()
    {
        return new Object[]{getCurrentValue()};
    }

    private static class GuiExponentialSlider extends GuiSlider
    {
        public GuiExponentialSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
        {
            super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr);
            updateSlider();
        }

        @Override
        public void updateSlider()
        {
            if (this.sliderValue < 0.0F)
            {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F)
            {
                this.sliderValue = 1.0F;
            }

            if (Math.abs(this.sliderValue - 0.5) < 0.006)
                this.sliderValue = 0.5;

            if (drawString)
            {
                String val = Integer.toString((int) Math.round(100 * Math.pow(10, getValue())));
                displayString = dispString + val + suffix;
            }

            if (parent != null)
            {
                parent.onChangeSliderValue(this);
            }
        }
    }
}
*/