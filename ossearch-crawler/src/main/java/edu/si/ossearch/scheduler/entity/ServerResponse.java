package edu.si.ossearch.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerResponse {
	private int statusCode;
	private String message;
	private Object data;
}

