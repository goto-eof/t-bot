package org.andreidodu.tbot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MessageStorage {
	List<Message> messages = new ArrayList<>();
}
