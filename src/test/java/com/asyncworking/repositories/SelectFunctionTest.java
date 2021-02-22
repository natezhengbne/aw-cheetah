package com.asyncworking.repositories;

import com.asyncworking.AwCheetahApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
public class SelectFunctionTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void selectEmailTest(){
    }
}
