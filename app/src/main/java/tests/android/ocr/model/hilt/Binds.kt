package tests.android.ocr.model.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import tests.android.ocr.model.datasource.OutputDatasource
import tests.android.ocr.model.datasource.ParamsDatasource
import tests.android.ocr.model.datasource.PerceptronDatasource
import tests.android.ocr.model.datasource.SampleDatasource
import tests.android.ocr.model.repository.OutputRepository
import tests.android.ocr.model.repository.ParamsRepository
import tests.android.ocr.model.repository.PerceptronRepository
import tests.android.ocr.model.repository.SampleRepository

@Module
@InstallIn(ViewModelComponent::class)
interface Binds {

    @Binds
    fun bindOutputRepository(datasource: OutputDatasource): OutputRepository

    @Binds
    fun bindPerceptronRepository(datasource: PerceptronDatasource): PerceptronRepository

    @Binds
    fun bindSampleRepository(datasource: SampleDatasource): SampleRepository

    @Binds
    fun bindParamsRepository(datasource: ParamsDatasource): ParamsRepository

}