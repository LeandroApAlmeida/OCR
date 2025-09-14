package tests.android.ocr.model.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tests.android.ocr.database.AppDatabase
import tests.android.ocr.database.dao.LearningDao
import tests.android.ocr.database.dao.OutputDao
import tests.android.ocr.database.dao.ParamsDao
import tests.android.ocr.database.dao.SampleDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Providers {

    @Provides fun provideAppDatabase(): AppDatabase {
        return AppDatabase.instance
    }

    @Provides fun provideLearning(appDatabase: AppDatabase): LearningDao {
        return appDatabase.getLearningDao()
    }

    @Provides fun provideOutput(appDatabase: AppDatabase): OutputDao {
        return appDatabase.getOutputDao()
    }

    @Provides fun provideSample(appDatabase: AppDatabase): SampleDao {
        return appDatabase.getSampleDao()
    }

    @Provides fun provideParams(appDatabase: AppDatabase): ParamsDao {
        return appDatabase.getParamsDao()
    }

}