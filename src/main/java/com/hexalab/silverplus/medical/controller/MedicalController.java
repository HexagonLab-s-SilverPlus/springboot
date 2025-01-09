package com.hexalab.silverplus.medical.controller;

import com.hexalab.silverplus.common.Search;
import com.hexalab.silverplus.medical.model.dto.Medical;
import com.hexalab.silverplus.medical.model.service.MedicalService;
import com.hexalab.silverplus.member.model.dto.Member;
import com.hexalab.silverplus.member.model.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/medical")
@CrossOrigin
public class MedicalController {
    //service DI
    private final MedicalService medicalService;
    private final MemberService memberService;

    //Medical list
//    @GetMapping("/medical/{mediSnrUUID}")
//    public ResponseEntity<Map<String, Object>> selectMedicalList(
//            @PathVariable String mediSnrUUID
//    ) {
//        log.info("mediSnrUUID: {}", mediSnrUUID);
//
//        try {
//            Member snrMember = memberService.selectMember(mediSnrUUID);
//
//            ArrayList<Medical> medicalList = medicalService.selectAllMedicalList(snrMember.getMemUUID());
//            if (medicalList == null || medicalList.isEmpty()) {
//                return ResponseEntity.ok(Map.of("list", new ArrayList<>()));
//            }
//
//            log.info("medicalList: {}", medicalList);
//            log.info("medicalList size: {}", medicalList.size());
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("list", medicalList);
//
//            return ResponseEntity.ok(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }//selectMedicalList end

    @GetMapping("/{mediSnrUUID}")
    public ResponseEntity<Map<String, Object>> selectMedicalList(
            @RequestParam String mediSnrUUID,
            @ModelAttribute Search search
    ) {
        log.info("mediSnrUUID: {}", mediSnrUUID);
        log.info("Search parameters: {}", search);

        Pageable pageable = PageRequest.of(search.getPageNumber() - 1,
                search.getPageSize(), Sort.by(Sort.Direction.DESC, "mediDiagDate"));
        try {
            ArrayList<Medical> medicalList = medicalService.selectAllMedicalList(mediSnrUUID, pageable);
            search.setListCount(medicalService.selectAllCount(mediSnrUUID));

            if (medicalList == null || medicalList.isEmpty()) {
                return ResponseEntity.ok(Map.of("list", new ArrayList<>()));
            }

            Map<String, Object> map = new HashMap<>();
            map.put("list", medicalList);
            map.put("search", search);

            return ResponseEntity.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }//selectMedicalList end

    //Save Medical
    @PostMapping("/{mediSnrUUID}")
    public ResponseEntity<Map<String, Object>> insertMedical(
            @PathVariable String mediSnrUUID,
            @RequestBody Medical medical
    ) {
        log.info("Save Medical - mediSnrUUID: {}, medical: {}", mediSnrUUID, medical);

        try {
            Member snrMember = memberService.selectMember(mediSnrUUID);

            medical.setMediSnrUUID(mediSnrUUID);
            medical.setMediMgrUUID(snrMember.getMemUUIDMgr());
            medical.setMediId(UUID.randomUUID().toString());
            //공개
            medical.setMediCreatedAt(new Timestamp(System.currentTimeMillis()));

            //save
            if (medicalService.insertMedical(medical) > 0) {
                log.info("Medical inserted");
            } else {
                log.info("Medical insert failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }//insertMedical end

    //isPublic update
    @PutMapping("/privacy/{mediSnrUUID}")
    public ResponseEntity<Map<String, Object>> updateMedicalPrivacy(
        @PathVariable String mediSnrUUID,
        @RequestBody Map<String, String> requestBody
    ) {
        log.info("Update Medical Privacy - mediSnrUUID: {}, mediPrivacy: {}", mediSnrUUID, requestBody.get("mediPrivacy"));

        try {
            String mediPrivacy = requestBody.get("mediPrivacy");
            Member snrMember = memberService.selectMember(mediSnrUUID);

            if (medicalService.updateMedicalPrivacy(mediSnrUUID, mediPrivacy) > 0) {
                log.info("Medical Privacy updated");
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                log.info("Medical Privacy update failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }//updateMedicalPrivacy end

    //Update Medical
    @PutMapping("/{mediSnrUUID}")
    public ResponseEntity<Map<String, Object>> updateMedical(
            @PathVariable String mediSnrUUID,
            @RequestBody Medical updatedMedical
    ) {
        log.info("Update Medical - mediSnrUUID: {}, updatedMedical: {}", mediSnrUUID, updatedMedical);

        try {
            //변경전 정보
            Medical prevMedical = medicalService.selectMedicalBymediId(updatedMedical.getMediId());
            if (prevMedical == null) {
                log.info("prevMedical is null");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            //업데이트
            updatedMedical.setMediUpdatedAt(new Timestamp(System.currentTimeMillis()));
            if (medicalService.updateMedical(updatedMedical) > 0) {
                log.info("Medical updated");
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                log.info("Medical update failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }//updateMedical end

    //Delete Medical
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteMedicals(
            @RequestBody List<String> mediIds
    ) {
        log.info("Delete Selected Medicals mediIds: {}", mediIds);

        try {
            int deletedCount = medicalService.deleteMedicals(mediIds);

            if (deletedCount == mediIds.size()) {
                log.info("Medical deleted");
                return ResponseEntity.ok(Map.of("deletedCount", deletedCount));
            } else {
                log.info("Some medical delete failed");
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(Map.of("deletedCount", deletedCount));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }//deleteMedicals end

}//MedicalController end
