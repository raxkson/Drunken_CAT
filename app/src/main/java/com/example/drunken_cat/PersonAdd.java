package com.example.drunken_cat;
import java.util.Map.Entry;
import java.util.*;

public class PersonAdd {
    private String Msg = "추가완료";
    // 중복검사
    // 배열 지인 등록 최대 100
    // 오른차순 정렬 내놓고 지인 중복 검사시 이진탐색
    // 예를들어 100명으로 지정해놓으면 "오류 출력하고" 제거기능 추가하나 ?
    Map<String,String> person = new HashMap<String,String>(); // 지인 저장할 배열

    PersonAdd(String name_ , String phone_){
        if(DuplicationVerify(name_,phone_)) {// 중복검사
            Msg = "ADD COMPLETE";
            SORT();
        }
        else
            Msg = "이름이나 휴대폰이 중복";
   }
   //정렬 메소드

   void SORT(){
       List<String> keySetList = new ArrayList<>(person.keySet());
       // 내림차순 //
       Collections.sort(keySetList, new Comparator<String>() {
           public int compare(String o1, String o2) {
               return person.get(o1).compareTo(person.get(o2));
           }
       });
   }
   boolean DuplicationVerify(String n, String p){
       if(person.containsKey(n) && person.containsValue(p))// 이름과 폰이 둘다 있으면 !중복!
           return false;
       else if( person.containsValue(p)) // 폰만 있으도 중복
           return false;
        else {  // 이름만있으면 중복이 아님.
           person.put(n,p);
           return true;
       }
  }


}
