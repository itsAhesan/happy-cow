package com.xworkz.happycow.service;

import com.xworkz.happycow.dto.AgentDTO;
import com.xworkz.happycow.dto.BankForm;
import com.xworkz.happycow.dto.PhotoDTO;
import com.xworkz.happycow.entity.AgentBankEntity;
import com.xworkz.happycow.entity.AgentEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.DoubleStream;

public interface AgentService {

    List<AgentDTO> getAllAgents(int pageNumber, int pageSize);

    void registerAgent(AgentDTO agentDTO, String adminName);

    AgentDTO findById(Integer id);

    boolean updateAgent(AgentDTO agentDTO,String adminName);

    boolean deleteAgent(Integer id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    long getAgentCount();

    List<AgentDTO> searchAgents(String trim, int page, int size);

    long getAgentSearchCount(String trim);

    List<String> getAllMilkTypes();

    AgentDTO getAgentByPhoneNumber(String phoneNumber);

    boolean sendOtp(String email);

    boolean verifyOtp(String email, String otp);

    AgentEntity findByEmail(String email);

    void updateFromDto(String emailOfLoggedInUser, AgentDTO dto, MultipartFile imageFile);

    PhotoDTO findPhotoById(Integer id);

    void clearPhoto(Integer id, String requesterEmail);

    AgentEntity findByEmailEntity(String email);

    AgentBankEntity saveFirstTime(BankForm bankForm, String createdBy,Integer agentId,String email);

    boolean existsByAgentId(Integer agentId);

    AgentBankEntity findByAgentId(Integer agentId);

    void saveOrUpdateBankDetails(BankForm form);

    AgentEntity findByToken(String token);

    //AgentDTO findByEmail(String email);

}
