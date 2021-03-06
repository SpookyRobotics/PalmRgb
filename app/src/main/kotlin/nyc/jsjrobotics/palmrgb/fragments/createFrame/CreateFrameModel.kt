package nyc.jsjrobotics.palmrgb.fragments.createFrame

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.palmrgb.Application
import nyc.jsjrobotics.palmrgb.dataStructures.Palette
import nyc.jsjrobotics.palmrgb.fragments.connectionStatus.ConnectionStatusModel
import nyc.jsjrobotics.palmrgb.fragments.dialogs.selectPalette.SelectPaletteModel
import nyc.jsjrobotics.palmrgb.globalState.DeviceConstants
import nyc.jsjrobotics.palmrgb.globalState.SavedPaletteModel
import nyc.jsjrobotics.palmrgb.globalState.SharedPreferencesManager
import nyc.jsjrobotics.palmrgb.service.PalmRgbBackground
import nyc.jsjrobotics.palmrgb.service.remoteInterface.HackdayLightsInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateFrameModel @Inject constructor(private val application: Application,
                                           private val savedPalettesModel: SavedPaletteModel,
                                           private val selectPaletteModel: SelectPaletteModel,
                                           private val connectionStatusModel: ConnectionStatusModel,
                                           private val sharedPreferencesManager: SharedPreferencesManager){
    private var remoteDisplayEnabled = true
    var selectedPalette: Palette = savedPalettesModel.getStandardPalette()
    var displayedColors : MutableList<Int> = initialValues() ; private set
    var usingLargeArray : Boolean = true ; set (value) {
        field = value
        matrixChangedDisposable.onNext(field)
    }


    fun diodeRange() : IntRange = if (usingLargeArray) DeviceConstants.largeArrayRange else DeviceConstants.smallArrayRange

    fun saveDiodeState(index: Int, color: Int) {
        displayedColors[index] =  color
        if (remoteDisplayEnabled ){
            updateRemoteDisplay()
        }
    }

    fun updateRemoteDisplay() {
        if (sharedPreferencesManager.getSendLiveUpdatesToHardware() && connectionStatusModel.isConnected()) {
            HackdayLightsInterface.upload(displayedColors)
        }
    }

    private val paletteChangedDisposable: Disposable = selectPaletteModel.onPaletteSelected
            .subscribe{ palette ->
                selectedPalette = palette
            }

    private val matrixChangedDisposable: PublishSubject<Boolean> = PublishSubject.create()
    val onMatrixChangeSelected : Observable<Boolean> = matrixChangedDisposable

    fun writeCurrentFrameToDatabase(frameTitle: String) {
        val data = ArrayList<Int>()
        data.addAll(displayedColors)
        val intent = PalmRgbBackground.saveRgbFrame(frameTitle, usingLargeArray, data)
        application.startService(intent)
    }

    fun beginReset() {
        disableRemoteDisplay()
        displayedColors = initialValues()
    }

    fun disableRemoteDisplay() {
        remoteDisplayEnabled = false
    }

    fun endReset() {
        enableRemoteDisplay()
        updateRemoteDisplay()
    }

    fun enableRemoteDisplay() {
        remoteDisplayEnabled = true
    }

    private fun initialValues(): MutableList<Int>  = DeviceConstants.largeArrayRange.map { selectedPalette.colors.first() }.toMutableList()

    fun setDisplayingColors(displayingColors: List<Int>) {
        beginReset()
        displayingColors.forEachIndexed { index, color -> displayedColors[index] = color }
        endReset()
    }
}
