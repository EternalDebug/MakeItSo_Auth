/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.screens.about_user

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.makeitso.R.string as AppText
import com.example.makeitso.SETTINGS_SCREEN
import com.example.makeitso.SIGN_UP_SCREEN
import com.example.makeitso.TASK_ID
import com.example.makeitso.common.ext.idFromParameter
import com.example.makeitso.common.ext.isValidEmail
import com.example.makeitso.common.ext.isValidPassword
import com.example.makeitso.common.ext.passwordMatches
import com.example.makeitso.common.snackbar.SnackbarManager
import com.example.makeitso.model.Task
import com.example.makeitso.model.UData
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutUserViewModel @Inject constructor(
  private val accountService: AccountService,
  private val storageService: StorageService,
  logService: LogService
) : MakeItSoViewModel(logService) {

  var uiState = mutableStateOf(AboutUserUiState())
    private set

  var dataCopy: UData = UData("","","","")
  init {

    var mail = accountService.GetMail
    if (mail == null)
      uiState.value.login = "Amogus"
    else
      uiState.value.login = mail

    var is_ver = accountService.GetType
    if (is_ver != null)
      uiState.value.auth_type = is_ver
      else uiState.value.auth_type = "Amogus"


    launchCatching {
      storageService.myData
        .collect{
        dataCopy = UData(it.id,it.name,it.birthDate,it.userId)
          uiState.value = uiState.value.copy(name = dataCopy.name, birth = dataCopy.birthDate)
      }

    }

  }

  private val login
    get() = uiState.value.login
  private val auth_type
    get() = uiState.value.auth_type

  fun onNameChange(newValue: String) {
    uiState.value = uiState.value.copy(name = newValue)
  }

  fun onBirthChange(newValue: String) {
    uiState.value = uiState.value.copy(birth = newValue)
  }


  fun onUpdateUDataClick(openAndPopUp: (String, String) -> Unit) {
    var isItExist = false

    isItExist = dataCopy.userId != ""

    if (isItExist){
      launchCatching {
      storageService.updateUData(UData(dataCopy.id, uiState.value.name, uiState.value.birth, dataCopy.userId))}
    }
    else{
      launchCatching {
        storageService.saveUData(UData(name = uiState.value.name, birthDate = uiState.value.birth, userId = accountService.currentUserId ))
      }
    }
    SnackbarManager.showMessage(AppText.data_updated)
  }
}
