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

    public int compareTo(String priority,String previousPriority){
       return priority==null&&previousPriority!=null?1:
                priority!=null&&previousPriority==null?-1:
                        priority==null&&previousPriority==null?-1:
                                sortListArrange(priority,previousPriority);
        }

        public int sortListArrange(String priority,String previousPriority){
            List<String> sortList =  Arrays.asList("High","Medium","Low");
        for(String sort : sortList){
            if(sort.equals(priority) || sort.equals(previousPriority)){
                return priority.equals(previousPriority)?0:
                        sort.equals(priority)?-1:1;
            }
                }
                return 0;
        }

    }

