package com.banking.account_cmd.api.dto;

import com.banking.account_command.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositFundsResponse extends BaseResponse {

    private String id;

    public DepositFundsResponse(String message, String id) {
        super(message);
        this.id = id;
    }
}
