package eu.dakirsche.torchy;

import android.widget.SeekBar;

/**
 * Created with IntelliJ IDEA.
 * User: Cherry
 * Date: 17.12.13
 * Time: 08:55
 * To change this template use File | Settings | File Templates.
 */
public class ColorSeekbarChangeListener implements SeekBar.OnSeekBarChangeListener {
        private TorchyMainScreen parentActivity;

        public ColorSeekbarChangeListener(TorchyMainScreen parentActivity){
            this.parentActivity = parentActivity;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.parentActivity.onColorHasChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
}
