package com.bytser.template.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bytser.template.dtos.requests.CreateExampleRequest;
import com.bytser.template.dtos.requests.UpdateExampleRequest;
import com.bytser.template.dtos.responses.ApiErrorResponse;
import com.bytser.template.dtos.responses.ExampleStatsResponse;
import com.bytser.template.services.ExampleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("example")
public class ExampleController {

    private final ExampleService exampleService;

	public ExampleController(ExampleService exampleService) {
		this.exampleService = exampleService;
	}

    @Operation(summary = "Register a user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created - Succes"),
        @ApiResponse(responseCode = "400", description = "Bad request - Ongeldige of ontbrekende input data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createExample(
            @Valid @RequestBody CreateExampleRequest example) {
        exampleService.addExample(example);
    }

    @Operation(summary = "Update a user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created - Succes"),
        @ApiResponse(responseCode = "400", description = "Bad request - Ongeldige of ontbrekende input data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))
        )
    })
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateExampleRequest updateExample
    ) {
        exampleService.updateExample(userId, updateExample);
    }

    @Operation(summary = "User statistics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ok - Succes",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExampleStatsResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public ExampleStatsResponse getExampleStats() {
        return exampleService.getExampleStats();
    }
}
