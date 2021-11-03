package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LogWrapperDTO {

	List<LogRowDTO> rows = new ArrayList<>();

}
