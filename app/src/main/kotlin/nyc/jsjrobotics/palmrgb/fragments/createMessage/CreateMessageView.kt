package nyc.jsjrobotics.palmrgb.fragments.createMessage

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import io.reactivex.disposables.Disposable
import nyc.jsjrobotics.palmrgb.R
import nyc.jsjrobotics.palmrgb.customViews.XmlDiodeArray
import nyc.jsjrobotics.palmrgb.inflate
import javax.inject.Inject

class CreateMessageView @Inject constructor(val model: CreateMessageModel,
                                            val largeDiodeArray: XmlDiodeArray ) : LifecycleObserver {
    lateinit var rootXml: View

    private lateinit var largeMatrix: ConstraintLayout
    private lateinit var smallMatrix: ConstraintLayout
    private lateinit var input: EditText
    private var displayChangedDisposable: Disposable? = null

    fun initView(container: ViewGroup, savedInstanceState: Bundle?) {
        rootXml = container.inflate(R.layout.fragment_create_message)
        largeMatrix = rootXml.findViewById(R.id.rgb_matrix64)
        largeDiodeArray.setView(largeMatrix)
        smallMatrix = rootXml.findViewById(R.id.rgb_matrix32)
        smallMatrix.visibility = View.GONE
        input = rootXml.findViewById(R.id.message_text)
        displayChangedDisposable = model.onDisplayChanged.subscribe{
            largeDiodeArray.showColors(model.displayedColors)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        displayChangedDisposable?.dispose()
    }

    fun setInputListener(textWatcher : TextWatcher) {
        input.addTextChangedListener(textWatcher)
    }

}
