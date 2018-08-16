package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.*;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ClientResolver {

    //------------GETs----------------

    /**
     * Queries the server for the clients organ donation status.
     * @param client The client to retrieve the data from.
     */
    Map<Organ, Boolean> getOrganDonationStatus(Client client);

    /**
     * Queries the server for the clients transplant requests.
     * @param client The client to retrieve the data from.
     */
    List<TransplantRequest> getTransplantRequests(Client client);

    /**
     * Queries the server for the clients medication records.
     * @param client The client to retrieve the data from.
     */
    List<MedicationRecord> getMedicationRecords(Client client);

    List<ProcedureRecord> getProcedureRecords(Client client);

    List<IllnessRecord> getIllnessRecords(Client client);

    Collection<DonatedOrgan> getDonatedOrgans(Client client);

    List<HistoryItem> getHistory(Client client);

    //------------POSTs----------------

    Client createClient(CreateClientView createClientView);

    List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request);

    List<IllnessRecord> addIllnessRecord(Client client, CreateIllnessView createIllnessView);

    List<MedicationRecord> addMedicationRecord(Client client, CreateMedicationRecordView medicationRecordView);

    List<ProcedureRecord> addProcedureRecord(Client client, CreateProcedureView procedureView);

    DonatedOrgan manuallyOverrideOrgan(DonatedOrgan donatedOrgan, String overrideReason);

    //------------PATCHs----------------

    Map<Organ, Boolean> modifyOrganDonation(Client client, Map<Organ, Boolean> changes);

    TransplantRequest resolveTransplantRequest(Client client, TransplantRequest request,
            ResolveTransplantRequestObject resolveTransplantRequestObject);

    Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject);

    IllnessRecord modifyIllnessRecord(Client client, IllnessRecord toModify, ModifyIllnessObject modifyIllnessObject);

    MedicationRecord modifyMedicationRecord(Client client, MedicationRecord record, LocalDate stopDate);

    ProcedureRecord modifyProcedureRecord(Client client, ProcedureRecord toModify,
            ModifyProcedureObject modifyProcedureObject);

    DonatedOrgan editManualOverrideForOrgan(DonatedOrgan donatedOrgan, String newOverrideReason);

    //------------DELETEs----------------

    void deleteIllnessRecord(Client client, IllnessRecord record);

    void deleteProcedureRecord(Client client, ProcedureRecord record);

    void deleteMedicationRecord(Client client, MedicationRecord record);

    DonatedOrgan cancelManualOverrideForOrgan(DonatedOrgan donatedOrgan);
}
