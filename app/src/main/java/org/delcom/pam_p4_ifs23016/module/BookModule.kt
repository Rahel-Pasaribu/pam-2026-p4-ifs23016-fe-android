package org.delcom.pam_p4_ifs23016.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.delcom.pam_p4_ifs23016.network.books.service.IBookAppContainer
import org.delcom.pam_p4_ifs23016.network.books.service.IBookRepository
import org.delcom.pam_p4_ifs23016.network.books.service.BookAppContainer
@Module
@InstallIn(SingletonComponent::class)
object BookModule {
    @Provides
    fun provideBookContainer(): IBookAppContainer {
        return BookAppContainer()
    }

    @Provides
    fun provideBookRepository(container: IBookAppContainer): IBookRepository {
        return container.bookRepository
    }
}