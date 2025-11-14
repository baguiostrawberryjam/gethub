// File: com.example.gethub.requests.RequestViewModel.java (FIXED)
package com.example.gethub.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gethub.data.DataRepository;
import com.example.gethub.models.RequestTicket;
import com.example.gethub.models.SystemDocument;
import com.example.gethub.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RequestViewModel extends ViewModel {

    // --- Core Request Data ---
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<RequestTicket> currentRequest = new MutableLiveData<>();

    // --- Validation and Navigation State (Updated TOTAL_PAGES) ---
    private final MutableLiveData<Boolean> page1Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> page2Valid = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> navigateToPage = new MutableLiveData<>();
    private final MutableLiveData<RequestTicket> requestCompleteEvent = new MutableLiveData<>();
    private static final int TOTAL_PAGES = 2; // REDUCED TO 2 PAGES

    // --- Page 1 LiveData (Delivery & Document) ---
    private final MutableLiveData<String> selectedDeliveryMethod = new MutableLiveData<>("");
    private final MutableLiveData<String> errDeliveryMethod = new MutableLiveData<>("");
    private final MutableLiveData<SystemDocument> selectedDocument = new MutableLiveData<>();
    private final MutableLiveData<List<String>> availableDocumentNames = new MutableLiveData<>();
    private final MutableLiveData<Double> currentFee = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> errDocumentType = new MutableLiveData<>("");
    private List<SystemDocument> allAvailableDocuments;

    // --- Page 2 LiveData (Purpose) ---
    private final MutableLiveData<String> selectedPurpose = new MutableLiveData<>("");
    private final MutableLiveData<String> otherPurposeText = new MutableLiveData<>("");
    private final MutableLiveData<String> errPurpose = new MutableLiveData<>("");

    // Hardcoded placeholder string to avoid R.string access in ViewModel
    private static final String DOCUMENT_PLACEHOLDER = "Select Document Type"; // Ensure this constant exists!
    private static final String PURPOSE_PLACEHOLDER = "Select Purpose of Request";


    public RequestViewModel() {
        super();
        allAvailableDocuments = DataRepository.getSystemDocuments();

        // FIX: Use a minimal initialization that avoids passing empty strings for DocumentType and DeliveryMethod
        RequestTicket minimalTicket = new RequestTicket(generateMockTicketId(), "", "TEMP_DOC", "TEMP_DELIVERY", 0.0, false);
        currentRequest.setValue(minimalTicket);

        availableDocumentNames.setValue(new ArrayList<>(Arrays.asList(DOCUMENT_PLACEHOLDER)));
    }

    public void init(String studentId) {
        if (currentRequest.getValue() != null && currentRequest.getValue().getStudentId().isEmpty()) {
            // FIX 1: setStudentId in RequestTicket is needed. Assuming it exists.
            currentRequest.getValue().setStudentId(studentId);
            currentUser.setValue(DataRepository.getUserByStudentId(studentId));
        }
    }

    // --- Getters ---
    public LiveData<String> getSelectedDeliveryMethod() { return selectedDeliveryMethod; }
    public LiveData<SystemDocument> getSelectedDocument() { return selectedDocument; }
    public LiveData<List<String>> getAvailableDocumentNames() { return availableDocumentNames; }
    public LiveData<Double> getCurrentFee() { return currentFee; }
    public LiveData<Boolean> getPage1Valid() { return page1Valid; }
    public LiveData<String> getErrDeliveryMethod() { return errDeliveryMethod; }
    public LiveData<String> getErrDocumentType() { return errDocumentType; }
    public LiveData<Integer> getNavigateToPage() { return navigateToPage; }
    public LiveData<RequestTicket> getRequestCompleteEvent() { return requestCompleteEvent; }
    public LiveData<Boolean> getPage2Valid() { return page2Valid; }
    public LiveData<String> getSelectedPurpose() { return selectedPurpose; }
    public LiveData<String> getOtherPurposeText() { return otherPurposeText; }
    public LiveData<String> getErrPurpose() { return errPurpose; }


    // --- Page 1 Logic ---

    public void onDeliveryMethodSelected(String method) {
        selectedDeliveryMethod.setValue(method);
        errDeliveryMethod.setValue("");

        selectedDocument.setValue(null);
        currentFee.setValue(0.0);
        errDocumentType.setValue("Please select a document.");

        updateAvailableDocuments(method);

        page1Valid.setValue(isPage1Valid());
    }

    public void onDocumentTypeSelected(String docName) {
        if (DOCUMENT_PLACEHOLDER.equals(docName) || docName.isEmpty()) {
            selectedDocument.setValue(null);
            currentFee.setValue(0.0);
            errDocumentType.setValue("Please select a document.");
        } else {
            SystemDocument doc = allAvailableDocuments.stream()
                    .filter(d -> d.getDocName().equals(docName))
                    .findFirst()
                    .orElse(null);

            selectedDocument.setValue(doc);
            if (doc != null) {
                currentFee.setValue(doc.getServiceFee());
                errDocumentType.setValue("");
            }
        }
        page1Valid.setValue(isPage1Valid());
    }

    private void updateAvailableDocuments(String method) {
        List<String> names = allAvailableDocuments.stream()
                .filter(doc -> {
                    String constraint = doc.getDeliveryConstraint();
                    if ("Digital".equals(method)) {
                        return "Digital Only".equals(constraint) || "Both".equals(constraint);
                    } else if ("Pick-up".equals(method)) {
                        return "Pick-up Only".equals(constraint) || "Both".equals(constraint);
                    }
                    return false;
                })
                .map(SystemDocument::getDocName)
                .collect(Collectors.toList());

        names.add(0, DOCUMENT_PLACEHOLDER);
        availableDocumentNames.setValue(names);
    }

    private boolean isPage1Valid() {
        return !selectedDeliveryMethod.getValue().isEmpty() && selectedDocument.getValue() != null;
    }

    private void validatePage1() {
        if (selectedDeliveryMethod.getValue().isEmpty()) {
            errDeliveryMethod.setValue("Delivery Method is required.");
        }
        if (selectedDocument.getValue() == null) {
            errDocumentType.setValue("Document selection is required.");
        }
    }

    // --- Page 2 Logic ---
    public void onPurposeSelected(String purpose) {
        selectedPurpose.setValue(purpose);
        errPurpose.setValue("");
        page2Valid.setValue(isPage2Valid());
    }

    public void onOtherPurposeTextChanged(String text) {
        otherPurposeText.setValue(text.trim());
        if ("Others (Please Specify)".equals(selectedPurpose.getValue())) {
            errPurpose.setValue(text.trim().isEmpty() ? "Please specify your purpose." : "");
        } else {
            errPurpose.setValue("");
        }
        page2Valid.setValue(isPage2Valid());
    }

    private boolean isPage2Valid() {
        String purpose = selectedPurpose.getValue();
        if (purpose == null || purpose.isEmpty() || purpose.equals(PURPOSE_PLACEHOLDER)) return false;

        if ("Others (Please Specify)".equals(purpose)) {
            return otherPurposeText.getValue() != null && !otherPurposeText.getValue().isEmpty();
        }
        return true;
    }

    private void validatePage2() {
        if (selectedPurpose.getValue() == null || selectedPurpose.getValue().isEmpty() || selectedPurpose.getValue().equals(PURPOSE_PLACEHOLDER)) {
            errPurpose.setValue("Purpose of Request is required.");
        } else if ("Others (Please Specify)".equals(selectedPurpose.getValue())) {
            if (otherPurposeText.getValue() == null || otherPurposeText.getValue().isEmpty()) {
                errPurpose.setValue("Please specify your purpose.");
            }
        }
    }

    // --- Navigation Logic ---
    public void goToNextPage(int currentPage) {
        boolean pageIsValid = false;
        switch (currentPage) {
            case 0: pageIsValid = isPage1Valid(); if (!pageIsValid) validatePage1(); break;
            case 1: pageIsValid = isPage2Valid(); if (!pageIsValid) validatePage2(); break;
            default: pageIsValid = true; break;
        }

        if (pageIsValid) {
            if (currentPage < TOTAL_PAGES - 1) {
                navigateToPage.setValue(currentPage + 1);
            } else {
                submitRequest();
            }
        }
    }

    public void goToPreviousPage(int currentPage) { if (currentPage > 0) navigateToPage.setValue(currentPage - 1); }
    public void onNavigationComplete() { navigateToPage.setValue(null); }
    public void onRequestComplete() { requestCompleteEvent.setValue(null); } // FIX 2: Added missing method

    // --- Final Submission ---
    private void submitRequest() {
        RequestTicket finalTicket = currentRequest.getValue();
        SystemDocument doc = selectedDocument.getValue();

        if (finalTicket == null || doc == null || finalTicket.getStudentId().isEmpty()) return;

        finalTicket.setDocumentType(doc.getDocName());
        finalTicket.setDeliveryMethod(selectedDeliveryMethod.getValue());
        finalTicket.setServiceFee(doc.getServiceFee());
        finalTicket.setInstant(doc.isInstant());

        String purpose = selectedPurpose.getValue();
        if ("Others (Please Specify)".equals(purpose)) {
            finalTicket.setPurposeOfRequest("Other: " + otherPurposeText.getValue());
        } else {
            finalTicket.setPurposeOfRequest(purpose);
        }

        DataRepository.addRequestTicket(finalTicket);
        requestCompleteEvent.setValue(finalTicket);
    }

    private String generateMockTicketId() {
        Random random = new Random();
        return String.format("REQ-%04d", random.nextInt(10000));
    }
}