package tdtu.edu.vn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdtu.edu.vn.model.ActivationCode;
import tdtu.edu.vn.service.ebook.ActivationCodeService;

import java.util.List;

@RestController
@RequestMapping("/activation")
public class ActivationCodeController {
    @Autowired
    private ActivationCodeService activationCodeService;


}
