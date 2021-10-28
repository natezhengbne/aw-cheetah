package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTodoItemDto {
    private Long todoItemId;

    private String description;

    private String projectTitle;

    private String priority;

    private OffsetDateTime dueDate;

    public int compareTo(String Priority,String PreviousPriority){
         List<String> sortList =  Arrays.asList("High","Medium","Low");
            if(Priority == null && PreviousPriority != null){
                return 1;
            }else if(Priority !=null && PreviousPriority == null){
                return -1;
            }else if(Priority == null && PreviousPriority == null){
                return -1;
            }else{
                for(String sort : sortList){
                    if(sort.equals(Priority) || sort.equals(PreviousPriority)){
                        if(Priority.equals(PreviousPriority)){
                            return 0;
                        }else if(sort.equals(Priority)){
                            return -1;
                        }else{
                            return 1;
                        }
                    }
                }
                return 0;
            }
        }
    }

