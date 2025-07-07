package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private MyOrderRepository myorderRepository;
    
    @ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME : " + userName);

		User user = userRepository.getUserByUserName(userName);

		System.out.println("USER : " + user);

		model.addAttribute("user", user);
	}
    
    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal) {
        return "normal/user_dashboard"; // This should match the Thymeleaf template name
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact",new Contact());
        return "normal/add_contact_form";
    }
    
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 Principal principal,
                                 @RequestParam("profileImage") MultipartFile file,
                                 HttpSession session) {
        try {
            String name = principal.getName();
            User user = userRepository.getUserByUserName(name);

            // Handle file upload
            if (file.isEmpty()) {
                
                System.out.println("File is Empty");
                contact.setImage("contacts.png");
            } 
            else {
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is Uploaded");
            }

            // Link contact to user
            contact.setUser(user);
            user.getContacts().add(contact);

            userRepository.save(user);

            System.out.println("USER : " + contact);
            System.out.println("Added to database");

            // ✅ Success message in session
            session.setAttribute("message", new Message("Contact added successfully!", "success"));

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();

            // ❌ Error message in session
            session.setAttribute("message", new Message("Something went wrong. Try again!", "danger"));
        }

        return "normal/add_contact_form";
    }
    
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,
                               Model model,
                               Principal principal) {

        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        PageRequest pageable = PageRequest.of(page, 5);  // 5 contacts per page
        Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());
        model.addAttribute("title", "Show Contacts");

        return "normal/show_contacts";
    }

    
    
    @PostMapping("/clear-message")
    @ResponseBody
    public void clearMessage(HttpSession session) {
        session.removeAttribute("message");
    }
    
    @RequestMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId,
                                    Model model,
                                    Principal principal) {

        // Get logged-in user's username and object
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        // Fetch contact
        Optional<Contact> contactOptional = contactRepository.findById(cId);

        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();

            // ✅ Check if contact belongs to the current user
            if ( (user.getId()==contact.getUser().getId())) {
                model.addAttribute("contact", contact);
                return "normal/contact_detail";
            } else {
                // ❌ Not authorized, show custom error page
                model.addAttribute("error", "You are not authorized to view this contact.");
                return "normal/access_denied";
            }
        } else {
            // ❌ Contact not found
            model.addAttribute("error", "Contact not found.");
            return "normal/access_denied";
        }
    }
 // Delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,
                                Model model,
                                Principal principal,
                                HttpSession session) {

       
            Optional<Contact> contactOptional = contactRepository.findById(cId);
            
            if (contactOptional.isPresent()) {
            	
                Contact contact = contactOptional.get();

                String userName = principal.getName();
                User user = userRepository.getUserByUserName(userName);

                // Check if contact belongs to the logged-in user
                if ((user.getId()==contact.getUser().getId())) {

                    // Unlink contact from user before deletion
//                    contact.setUser(null);
                	user.getContacts().remove(contact);
                    userRepository.save(user);

                    session.setAttribute("message", new Message("Contact deleted successfully.", "success"));
                } else {
                    session.setAttribute("message", new Message("You are not allowed to delete this contact.", "danger"));
                    return "normal/access_denied";
                }
            } else {
                session.setAttribute("message", new Message("Contact not found.", "warning"));
            }

        return "redirect:/user/show-contacts/0";
    }
    
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid, Model m, Principal principal) {

        // Get logged-in user's username
        String userName = principal.getName();
        User user = userRepository.getUserByUserName(userName);

        // Get contact by ID
        Optional<Contact> contactOptional = contactRepository.findById(cid);

        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();

            // ✅ Check if the contact belongs to the current user
            if (user.getId() == contact.getUser().getId()) {
                m.addAttribute("title", "Update Contact");
                m.addAttribute("contact", contact);  // ✅ Send contact to update_form.html
                return "normal/update_form";
            } else {
                m.addAttribute("error", "Unauthorized access to contact.");
                return "normal/access_denied";
            }

        } else {
            m.addAttribute("error", "Contact not found.");
            return "normal/access_denied";
        }
    }
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                HttpSession session,
                                Principal principal) {

        try {
        	//old contact details
        	Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
         
            //  Update image only if a new image is uploaded
            if (!file.isEmpty()) {
            	//delete old photo
            	 File deleteFile = new ClassPathResource("static/img").getFile();
            	File file1=new File(deleteFile,oldcontactDetail.getImage());
            	file1.delete();
                // update new photo
            	  File saveFile = new ClassPathResource("static/img").getFile();
                  Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                  Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                  contact.setImage(file.getOriginalFilename());
                
            } 
            else {
            	contact.setImage(oldcontactDetail.getImage());
            }
            User user =userRepository.getUserByUserName(principal.getName());
            contact.setUser(user);
            // Save updated contact
            this.contactRepository.save(contact);

            // Success message
            session.setAttribute("message", new Message("Contact updated successfully!", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!", "danger"));
        }

        return "redirect:/user/contact/" + contact.getcId();

    }
    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        String username = principal.getName();
        User user = this.userRepository.getUserByUserName(username);
        
        // ✅ Add this line to check the image
        System.out.println("User image: " + user.getImageUrl());
        model.addAttribute("user", user);
        return "normal/profile";
    }
 // 👉 1. Show form when user clicks "Update Profile"
    @GetMapping("/update-profile")
    public String openUpdateForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.getUserByUserName(username);
        model.addAttribute("user", user);
        return "normal/update_profile";
    }

    // 👉 2. Handle POST form submission (image + data update)
    @PostMapping("/update-profile")
    public String handleProfileUpdate(@ModelAttribute User user,
                                      @RequestParam("profileImage") MultipartFile file,
                                      HttpSession session,
                                      Principal principal) {
        try {
            String username = principal.getName();
            User oldUser = userRepository.getUserByUserName(username);

            // Preserve essential data
            user.setId(oldUser.getId());
            user.setRole(oldUser.getRole());
            user.setContacts(oldUser.getContacts());

            // Handle file upload
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath(), file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                user.setImageUrl(file.getOriginalFilename());
            } else {
                user.setImageUrl(oldUser.getImageUrl());
            }

            userRepository.save(user);
            session.setAttribute("message", new Message("Profile Updated!", "success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong", "danger"));
        }

        return "normal/update_profile";
    }
    @GetMapping("/settings")
    public String openSettings() {
        return "normal/settings";
    }
 // Change password logic
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 HttpSession session) {

        // Get logged-in user
        String username = principal.getName();
        User currentUser = userRepository.getUserByUserName(username);

        // Check old password
        if (passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // Encode and set new password
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);

            session.setAttribute("message",new Message("✅ Password changed successfully!","success"));
        } else {
            session.setAttribute("message",new Message("❌ Old password is incorrect!","danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }
    @PostMapping("/create_order")
    @ResponseBody
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data, Principal principal) throws RazorpayException {
        System.out.println("Received Order Data: " + data);

        int amt = Integer.parseInt(data.get("amount").toString());

        RazorpayClient client = new RazorpayClient("rzp_test_SFcb5wLLTTOiib", "PjfxyjRWNEYpakEKNoheziWc");

        JSONObject ob = new JSONObject();
        ob.put("amount", amt * 100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_235425");

        Order order = client.orders.create(ob);
        System.out.println("Order created: " + order);

        // Save order to DB
        MyOrder myOrder = new MyOrder();
        myOrder.setAmount(order.get("amount").toString());
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setStatus("Created");
        myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
        myOrder.setReceipt(order.get("receipt"));

        this.myorderRepository.save(myOrder);

        // Respond with details
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.get("id"));
        response.put("amount", amt);
        response.put("currency", "INR");
        response.put("status", "created");

        return response;
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data) {

        MyOrder myOrder = this.myorderRepository.findByOrderId(data.get("order_id").toString());

        myOrder.setPaymentId(data.get("payment_id").toString());
        myOrder.setStatus(data.get("status").toString());

        this.myorderRepository.save(myOrder);

        return ResponseEntity.ok(Map.of("msg", "Order updated successfully"));
    }


}
