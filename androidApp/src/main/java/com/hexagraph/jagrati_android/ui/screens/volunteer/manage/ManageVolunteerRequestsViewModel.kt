package com.hexagraph.jagrati_android.ui.screens.volunteer.manage

import androidx.lifecycle.viewModelScope
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.model.volunteer.DetailedVolunteerRequestResponse
import com.hexagraph.jagrati_android.repository.volunteer.VolunteerRequestRepository
import com.hexagraph.jagrati_android.ui.screens.main.BaseViewModel
import com.hexagraph.jagrati_android.ui.screens.volunteer.VolunteerRequestUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageVolunteerRequestsViewModel(
    private val volunteerRequestRepository: VolunteerRequestRepository
) : BaseViewModel<ManageVolunteerRequestsUiState>() {

    private val _volunteerRequests = MutableStateFlow<List<DetailedVolunteerRequestResponse>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _selectedRequestId = MutableStateFlow<Long?>(null)
    private val _rejectionReason = MutableStateFlow<String>("")
    private val _showRejectionDialog = MutableStateFlow(false)
    private val _processingRequestId = MutableStateFlow<Long?>(null)
    private val _showDetailDialog = MutableStateFlow(false)

    override val uiState: StateFlow<ManageVolunteerRequestsUiState> = createUiStateFlow()

    init {
        loadVolunteerRequests()
    }

    override fun createUiStateFlow(): StateFlow<ManageVolunteerRequestsUiState> {
        return combine(
            _volunteerRequests,
            _isLoading,
            _selectedRequestId,
            _rejectionReason,
            _showRejectionDialog,
            _processingRequestId,
            _showDetailDialog,
            errorFlow,
            successMsgFlow
        ) { flows ->
            val requests = flows[0] as List<DetailedVolunteerRequestResponse>
            val isLoading = flows[1] as Boolean
            val selectedRequestId = flows[2] as Long?
            val rejectionReason = flows[3] as String
            val showRejectionDialog = flows[4] as Boolean
            val processingRequestId = flows[5] as Long?
            val showDetailDialog = flows[6] as Boolean
            val error = flows[7] as ResponseError?
            val successMsg = flows[8] as String?

            ManageVolunteerRequestsUiState(
                volunteerRequests = requests,
                isLoading = isLoading,
                selectedRequestId = selectedRequestId,
                rejectionReason = rejectionReason,
                showRejectionDialog = showRejectionDialog,
                processingRequestId = processingRequestId,
                showDetailDialog = showDetailDialog,
                selectedRequest = requests.find { it.id == selectedRequestId },
                error = error,
                successMessage = successMsg
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ManageVolunteerRequestsUiState()
        )
    }

    fun loadVolunteerRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            volunteerRequestRepository.getAllVolunteerRequests().collect { resource ->
                when {
                    resource.isLoading -> {
                        // Already handled at start
                    }
                    resource.isSuccess -> {
                        resource.data?.let { response ->
                            _volunteerRequests.value = response.requests
                        }
                        _isLoading.value = false
                    }
                    resource.isFailed -> {
                        emitError(resource.error)
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun approveRequest(requestId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _processingRequestId.value = requestId

            volunteerRequestRepository.approveVolunteerRequest(requestId).collect { resource ->
                when {
                    resource.isLoading -> {
                        // Already handled with processingRequestId
                    }
                    resource.isSuccess -> {
                        emitMsg("Request approved successfully")
                        loadVolunteerRequests() // Refresh the list
                        _processingRequestId.value = null
                    }
                    resource.isFailed -> {
                        emitError(resource.error)
                        _processingRequestId.value = null
                    }
                }
            }
        }
    }

    fun showRejectDialog(requestId: Long) {
        _selectedRequestId.value = requestId
        _rejectionReason.value = ""
        _showRejectionDialog.value = true
    }

    fun hideRejectDialog() {
        _showRejectionDialog.value = false
    }

    fun updateRejectionReason(reason: String) {
        _rejectionReason.value = reason
    }

    fun rejectRequest() {
        val requestId = _selectedRequestId.value ?: return
        val reason = _rejectionReason.value.takeIf { it.isNotBlank() }

        viewModelScope.launch(Dispatchers.IO) {
            _processingRequestId.value = requestId
            _showRejectionDialog.value = false

            volunteerRequestRepository.rejectVolunteerRequest(requestId, reason).collect { resource ->
                when {
                    resource.isLoading -> {
                        // Already handled with processingRequestId
                    }
                    resource.isSuccess -> {
                        emitMsg("Request rejected successfully")
                        loadVolunteerRequests() // Refresh the list
                        _processingRequestId.value = null
                    }
                    resource.isFailed -> {
                        emitError(resource.error)
                        _processingRequestId.value = null
                    }
                }
            }
        }
    }

    fun showDetailDialog(requestId: Long) {
        _selectedRequestId.value = requestId
        _showDetailDialog.value = true
    }

    fun hideDetailDialog() {
        _showDetailDialog.value = false
    }
}

data class ManageVolunteerRequestsUiState(
    val volunteerRequests: List<DetailedVolunteerRequestResponse> = emptyList(),
    val isLoading: Boolean = false,
    val selectedRequestId: Long? = null,
    val rejectionReason: String = "",
    val showRejectionDialog: Boolean = false,
    val processingRequestId: Long? = null,
    val showDetailDialog: Boolean = false,
    val selectedRequest: DetailedVolunteerRequestResponse? = null,
    val error: ResponseError? = null,
    val successMessage: String? = null
)
