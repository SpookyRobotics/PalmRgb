package nyc.jsjrobotics.palmrgb.fragments.createFrame

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import nyc.jsjrobotics.palmrgb.Application
import nyc.jsjrobotics.palmrgb.dataStructures.Palette
import nyc.jsjrobotics.palmrgb.fragments.connectionStatus.ConnectionStatusModel
import nyc.jsjrobotics.palmrgb.fragments.dialogs.selectPalette.SelectPaletteModel
import nyc.jsjrobotics.palmrgb.globalState.SavedPaletteModel
import nyc.jsjrobotics.palmrgb.service.PalmRgbBackground
import nyc.jsjrobotics.palmrgb.service.remoteInterface.HackdayLightsInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateFrameModel @Inject constructor(private val application: Application,
                                           private val savedPalettesModel: SavedPaletteModel,
                                           private val selectPaletteModel: SelectPaletteModel,
                                           private val connectionStatusModel: ConnectionStatusModel){
    var selectedPalette: Palette = savedPalettesModel.getStandardPalette()
    var displayedColors : MutableList<Int> = initialValues() ; private set
    var usingLargeArray : Boolean = true ; set (value) {
        field = value
        matrixChangedDisposable.onNext(field)
    }

    fun largeArrayRange() = IntRange(0, 63)
    fun smallArrayRange() = IntRange(0, 31)
    fun diodeRange() : IntRange = if (usingLargeArray) largeArrayRange() else smallArrayRange()

    fun saveDiodeState(index: Int, color: Int) {
        displayedColors[index] =  color
        if (connectionStatusModel.liveCreateFrameUpdates && connectionStatusModel.isConnected()) {
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

    fun reset() {
        displayedColors = initialValues()
    }

    private fun initialValues(): MutableList<Int>  = largeArrayRange().map { selectedPalette.colors.first() }.toMutableList()

    fun setDisplayingColors(displayingColors: List<Int>) {
        reset()
        displayingColors.forEachIndexed { index, color -> displayedColors[index] = color }
    }
}
