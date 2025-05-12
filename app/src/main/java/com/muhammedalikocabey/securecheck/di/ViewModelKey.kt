package com.muhammedalikocabey.securecheck.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Dagger multibinding içinde ViewModel'leri eşleştirmek için kullanılan custom key
 *
 * Custom map key for ViewModel multibinding with Dagger
 *
 * @since 11.05.2025
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)