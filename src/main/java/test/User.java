package test;

import anno.ORMColumn;
import anno.ORMConverter;
import anno.ORMTable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */
@Component
@ORMTable("user")
public class User {


    @ORMColumn("id")
    private int id;
    @ORMColumn("age")
    private int MyAge;

    @ORMConverter(MyConverter.class)
    private List<Integer> name;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMyAge() {
        return MyAge;
    }

    public void setMyAge(int myAge) {
        MyAge = myAge;
    }

   public List<Integer> getName() {
        return name;
   }

    public void setName(List<Integer> name) {
        this.name = name;
  }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", MyAge=" + MyAge +
                ", name=" + name +
                '}';
    }
}
