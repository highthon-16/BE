package com.ittae.be.repository

import com.ittae.be.model.CalendarEvent
import org.springframework.data.jpa.repository.JpaRepository

interface CalendarEventRepository : JpaRepository<CalendarEvent, Long>
