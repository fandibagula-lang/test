package org.babetech.borastock.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sunildhiman90.kmauth.google.GoogleAuthManager
import com.sunildhiman90.kmauth.google.KMAuthGoogle
import org.babe.sqldelight.data.db.AppDatabase
import org.babetech.borastock.data.db.provideDriver
import org.babetech.borastock.data.dispatchers.IODispatcher
import org.babetech.borastock.data.models.datasource.datastore.provideDataStore
import org.babetech.borastock.data.models.datasource.repository.BoraStockRepository
import org.babetech.borastock.data.models.datasource.repository.BoraStockRepositoryImpl
import org.babetech.borastock.data.repository.StockRepository
import org.babetech.borastock.data.repository.StockRepositoryImpl
import org.babetech.borastock.domain.usecase.AddStockEntryUseCase
import org.babetech.borastock.domain.usecase.AddStockExitUseCase
import org.babetech.borastock.domain.usecase.CreateStockItemUseCase
import org.babetech.borastock.domain.usecase.CreateSupplierUseCase
import org.babetech.borastock.domain.usecase.DeleteStockEntryUseCase
import org.babetech.borastock.domain.usecase.DeleteStockExitUseCase
import org.babetech.borastock.domain.usecase.DeleteStockItemUseCase
import org.babetech.borastock.domain.usecase.DeleteSupplierUseCase
import org.babetech.borastock.domain.usecase.GetAllStockEntriesUseCase
import org.babetech.borastock.domain.usecase.GetAllStockExitsUseCase
import org.babetech.borastock.domain.usecase.GetAllStockItemsUseCase
import org.babetech.borastock.domain.usecase.GetAllSuppliersUseCase
import org.babetech.borastock.domain.usecase.GetCurrentUserUseCase
import org.babetech.borastock.domain.usecase.GetRecentMovementsUseCase
import org.babetech.borastock.domain.usecase.GetStockEntriesByItemIdUseCase
import org.babetech.borastock.domain.usecase.GetStockExitsByItemIdUseCase
import org.babetech.borastock.domain.usecase.GetStockItemByIdUseCase
import org.babetech.borastock.domain.usecase.GetStockMovementsUseCase
import org.babetech.borastock.domain.usecase.GetStockStatisticsUseCase
import org.babetech.borastock.domain.usecase.GetSupplierByIdUseCase
import org.babetech.borastock.domain.usecase.GetSupplierStatisticsUseCase
import org.babetech.borastock.domain.usecase.GetSuppliersDistributionUseCase
import org.babetech.borastock.domain.usecase.SetCurrentUserUseCase
import org.babetech.borastock.domain.usecase.UpdateStockEntryUseCase
import org.babetech.borastock.domain.usecase.UpdateStockExitUseCase
import org.babetech.borastock.domain.usecase.UpdateStockItemUseCase
import org.babetech.borastock.domain.usecase.UpdateSupplierUseCase
import org.babetech.borastock.ui.screens.auth.viewmodel.LoginViewModel
import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
import org.babetech.borastock.ui.screens.screennavigation.Entries.EntriesViewModel
import org.babetech.borastock.ui.screens.screennavigation.StockViewModel
import org.babetech.borastock.ui.screens.screennavigation.exits.ExitsViewModel
import org.babetech.borastock.ui.screens.screennavigation.suppliers.SuppliersViewModel
import org.babetech.borastock.ui.screens.setup.viewmodel.CompanySetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    // Dispatcher
  single { IODispatcher }

    // DataStore multiplateforme
    single<DataStore<Preferences>> { provideDataStore() }

    // Repositories
    single<BoraStockRepository> { BoraStockRepositoryImpl(datastore = get(), datastoreUser = get()) }
    single<StockRepository> { StockRepositoryImpl(dispatcher = get()) }

    // SQLDelight
   single { provideDriver() }
   single { AppDatabase(driver = get()) }

    // Auth Manager Google
    single<GoogleAuthManager> { KMAuthGoogle.googleAuthManager }

    // Use Cases – User
    factory { GetCurrentUserUseCase(repository = get<BoraStockRepository>()) }
    factory { SetCurrentUserUseCase(repository = get<BoraStockRepository>()) }

    // Use Cases – Stock Items
    factory { GetAllStockItemsUseCase(repository = get<StockRepository>()) }
    factory { GetStockItemByIdUseCase(repository = get<StockRepository>()) }
    factory { CreateStockItemUseCase(repository = get<StockRepository>()) }
    factory { UpdateStockItemUseCase(repository = get<StockRepository>()) }
    factory { DeleteStockItemUseCase(repository = get<StockRepository>()) }

    // Use Cases – Stock Entries
    factory { GetAllStockEntriesUseCase(repository = get<StockRepository>()) }
    factory { GetStockEntriesByItemIdUseCase(repository = get<StockRepository>()) }
    factory { AddStockEntryUseCase(repository = get<StockRepository>()) }
    factory { UpdateStockEntryUseCase(repository = get<StockRepository>()) }
    factory { DeleteStockEntryUseCase(repository = get<StockRepository>()) }

    // Use Cases – Stock Exits
    factory { GetAllStockExitsUseCase(repository = get<StockRepository>()) }
    factory { GetStockExitsByItemIdUseCase(repository = get<StockRepository>()) }
    factory { AddStockExitUseCase(repository = get<StockRepository>()) }
    factory { UpdateStockExitUseCase(repository = get<StockRepository>()) }
    factory { DeleteStockExitUseCase(repository = get<StockRepository>()) }

    // Use Cases – Suppliers
    factory { GetAllSuppliersUseCase(repository = get<StockRepository>()) }
    factory { GetSupplierByIdUseCase(repository = get<StockRepository>()) }
    factory { CreateSupplierUseCase(repository = get<StockRepository>()) }
    factory { UpdateSupplierUseCase(repository = get<StockRepository>()) }
    factory { DeleteSupplierUseCase(repository = get<StockRepository>()) }


    factory { GetSupplierStatisticsUseCase(repository = get<StockRepository>()) }

    // Use Cases – Statistics
    factory { GetStockStatisticsUseCase(repository = get<StockRepository>()) }
    factory { GetRecentMovementsUseCase(repository = get<StockRepository>()) }

    // ViewModels
    viewModelOf(::LoginViewModel)
    viewModelOf(::CompanySetupViewModel)
    viewModelOf(::EntriesViewModel)
    viewModelOf(::ExitsViewModel)
    viewModelOf(::StockViewModel)
    viewModelOf(::AccueilViewModel)



    viewModelOf(::SuppliersViewModel)

    factory { GetStockMovementsUseCase(get()) }
    factory { GetStockStatisticsUseCase(get()) }
    factory { GetSuppliersDistributionUseCase(get()) }



}
