package com.wangqi;

import com.wangqi.dao.UserDao;
import com.wangqi.entity.PageCondition;
import com.wangqi.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class WaimaiApplicationTests {
@Autowired
    AdminService adminService;
@Test
    public  void test1(){
    PageCondition pg=new PageCondition();
    pg.setCurrentPage(1);
    pg.setPageSize(1);
    adminService.findByPageStore(pg);

}


}
