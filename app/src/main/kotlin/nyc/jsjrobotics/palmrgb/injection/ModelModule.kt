package nyc.jsjrobotics.palmrgb.injection

import dagger.Module
import dagger.Provides
import nyc.jsjrobotics.palmrgb.fragments.dialogs.changeDisplay.ChangeDisplayDialogModel
import nyc.jsjrobotics.palmrgb.fragments.dialogs.saveColor.SaveColorDialogModel
import nyc.jsjrobotics.palmrgb.fragments.dialogs.saveFrame.SaveRgbFrameDialogModel


@Module
class ModelModule {
    @Provides
    internal fun provideCreateFrameModel() : SaveRgbFrameDialogModel = SaveRgbFrameDialogModel.instance()

    @Provides
    internal fun provideChangeDisplayDialogModel() : ChangeDisplayDialogModel = ChangeDisplayDialogModel.instance()

    @Provides
    internal fun provideSaveColorDialogModel() : SaveColorDialogModel = SaveColorDialogModel.instance()

}